import bs4
import topscoreapiservices as ts
import topscoreoauth2 as to
import wumodel
import wumodel2
import datetime
import http.cookiejar as cookielib
import urllib
import mechanize
from mechanize import Browser

import concurrent.futures as fu
from functools import partial
import multiprocessing as mp
from multiprocessing import Process

def getBrowser(cookiejar):
    br = Browser()
    br.set_cookiejar( cookiejar )
    br.set_handle_equiv(True)
    br.set_handle_redirect(True)
    br.set_handle_referer(True)
    br.set_handle_robots(False)
    br.set_handle_refresh(mechanize._http.HTTPRefreshProcessor(), max_time=1)
    return br

    br.addheaders = [('User-agent', 'Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.1) Gecko/2008071615 Fedora/3.0.1-1.fc9 Firefox/3.0.1')]
def collectUserData(br):
    """br is an authenticated brower"""
    name = None
    img = None
    games = []
    events = []
    futures = []

    with fu.ThreadPoolExecutor(max_workers=2) as executor:

        futures.append(executor.submit(collectUserEvents, getBrowser(br.cookiejar)))

        response = br.open("https://wds.usetopscore.com/")
        soup = bs4.BeautifulSoup(response.read(), features="html5lib")

        for divtag in soup.findAll('div', class_='global-toolbar-item global-toolbar-item-full global-toolbar-user global-toolbar-item-right'):
            for imgtag in divtag.findAll('img', src=True, class_='global-toolbar-user-img'):
                img = imgtag['src'] #Collect users profile image

            for a in divtag.findAll('a', recursive=False):
                for spantag in a.findAll('span', class_='btn-label'):
                    name = spantag.text #Collect users name

            for a2 in divtag.findAll('a', class_='icon-schedule'):
                link_schedule = a2['href']

            futures.append(executor.submit(collectUserUpcomingGames, getBrowser(br.cookiejar), link_schedule))

        for future in fu.as_completed(futures):
                res =  future.result()
                if isinstance(res[0], wumodel2.Event):
                    events = res
                else:
                    games = res

    #return wumodel2.User(name, None, None, img)
    return wumodel2.User(name, events, games, img)

def collectUserUpcomingGames(br, link):
    response = br.open("https://wds.usetopscore.com{}".format(link))
    soup = bs4.BeautifulSoup(response.read(), features="html5lib")
    games = []

    for gamediv in soup.findAll('div', class_='game-list-item'):
        team_names = []
        team_images = []
        date = None
        time = None
        location = None
        field = None

        #get date and Time
        dtdiv = gamediv.find('div', class_='span2')
        date = dtdiv.find('span', recursive=True, class_='push-left').text
        time = dtdiv.find('div', recursive=True, class_='push-right').text

        #get names of teams in game
        for teamdiv in gamediv.findAll('div', class_='game-participant'):
            for teamnamediv in teamdiv.findAll('div', class_='schedule-team-name'):
                team_names.append(teamdiv.text.strip())
            for img in teamdiv.findAll('img', src=True):
                team_images.append(img['src'].replace('40', '200'))

        #get game location and field
        locationdiv = gamediv.findAll('div', class_='span2')[-1]
        locationspan = locationdiv.find('span', recursive=True, class_='push-left')
        field = locationspan.text.split()[3]
        location = locationspan.find('a', recursive=True, class_='plain-link').text

        game = wumodel2.Game(home_team=team_names[0], away_team=team_names[1], home_team_img=team_images[0], away_team_img=team_images[1], date=date.split()[1], time=time.split()[0], field=field, location=location)
        games.append(game)

    return games

def collectUserEvents(br):
    """br is an authenticated brower"""
    events = []
    response = br.open("https://wds.usetopscore.com")
    soup = bs4.BeautifulSoup(response.read(), features="html5lib")
    divtag = soup.findAll('div', class_='global-toolbar-item global-toolbar-item-full')[0]
    for navtag in divtag.findAll('nav', class_='global-toolbar-subnav'):
        with fu.ThreadPoolExecutor(max_workers=10) as executor:
            futures = []
            teams = []
            for a in navtag.findAll('a', href=True, class_='global-toolbar-subnav-img-item plain-link'):
                event_dict = {}
                for spantag in a.findAll('span', class_='global-toolbar-subnav-item-name'):
                    event_dict['name'] = spantag.text #Event name
                for imgtag in a.findAll('img', src=True):
                    event_dict['img'] = imgtag['src'].replace('40', '200') # Event image
                if(a['href'].startswith('/e/')):
                    futures.append(executor.submit(collectEventTeams, getBrowser(br.cookiejar), "{}/teams".format(a['href']), event_dict))

        for future in fu.as_completed(futures):
            event_dict = future.result()
            # for team in event_dict['teams']:
            #     print(team)
            events.append(wumodel2.Event(name=event_dict['name'], teams=event_dict['teams'], img=event_dict['img']))

    return events

def collectEventTeams(br, link, event_dict):
    #Collect teams from first 10 pages (there probably is only max 3 pages)
    """br is an authenticated brower"""
    with fu.ThreadPoolExecutor(max_workers=10) as executor:
        futures = []
        teams = []
        for i in range(1, 5):
            futures.append(executor.submit(collectEventTeamsPage, getBrowser(br.cookiejar), link, i, event_dict['name']))

        for future in fu.as_completed(futures):
            teams += future.result()

    event_dict['teams'] = teams
    return event_dict

def collectEventTeamsPage(br, link, pagenum, event_name):
    response = br.open("https://wds.usetopscore.com{}?page={}".format(link, pagenum))
    soup = bs4.BeautifulSoup(response.read(), features="html5lib")
    teams = []

    for row in soup.findAll('div', class_='row-fluid media-list-row'):
        for teamdiv in row.findAll('div', recursive=False):
            team_name = ""
            team_img = ""
            male_matchups = []
            female_matchups = []

            for h3 in teamdiv.findAll('h3'):
                team_name = h3.text #team name
                team_img = h3.find_parent('a')['style'][23:-2]

            for gender_cluster in teamdiv.findAll('li', class_='gender-cluster'):
                if gender_cluster.find('h5').text.startswith('Female Matchup'):
                    for a in gender_cluster.findAll('a'):
                        female_matchups.append(a.text)
                else:
                    for a in gender_cluster.findAll('a'):
                        male_matchups.append(a.text)

            team = wumodel2.Team(name=team_name, male_matchups=male_matchups, female_matchups=female_matchups, img=team_img, event_name=event_name)
            #print(team)
            teams.append(team)

    return teams
