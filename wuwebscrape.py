import bs4
import topscoreapiservices as ts
import topscoreoauth2 as to
import wumodel
import wumodel2
import datetime

def collectUserData(br):
    """br is an authenticated brower"""
    name = None
    img = None
    soup = bs4.BeautifulSoup(br.response().read(), features="html5lib")
    for divtag in soup.findAll('div', class_='global-toolbar-item global-toolbar-item-full global-toolbar-user global-toolbar-item-right'):
        for imgtag in divtag.findAll('img', src=True, class_='global-toolbar-user-img'):
            img = imgtag['src'] #Collect users profile image

        for a in divtag.findAll('a', recursive=False):
            for spantag in a.findAll('span', class_='btn-label'):
                name = spantag.text #Collect users name

    return wumodel2.User(name, collectUserEvents(br), img)

def collectUserEvents(br):
    """br is an authenticated brower"""
    events = []
    response = br.open("https://wds.usetopscore.com")
    soup = bs4.BeautifulSoup(response.read(), features="html5lib")

    for navtag in soup.findAll('nav', class_='global-toolbar-subnav'):
        for a in navtag.findAll('a', href=True, class_='global-toolbar-subnav-img-item plain-link'):
            for spantag in a.findAll('span', class_='global-toolbar-subnav-item-name'):
                event_name = spantag.text #Event name
            for imgtag in a.findAll('img', src=True):
                event_img = imgtag['src'] # Event image
            if(a['href'].startswith('/e/')):
                t1 = datetime.datetime.now()
                teams = collectEventTeams(br, "{}/teams".format(a['href']), 1)
                t2 = datetime.datetime.now()
                print('Time Taken: {}'.format(t2-t1))
                events.append(wumodel2.Event(name=event_name, teams=teams, img=event_img))
    return events

def collectEventTeams(br, link, pagenum):
    """br is an authenticated brower"""
    response = br.open("https://wds.usetopscore.com{}?page={}".format(link, pagenum))
    soup = bs4.BeautifulSoup(response.read(), features="html5lib")
    print("URL: {}".format(br.geturl()))
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

            team = wumodel2.Team(name=team_name, male_matchups=male_matchups, female_matchups=female_matchups, img=team_img)
            teams.append(team)

    for ul in soup.findAll('ul', class_='nav-pager align-left'):
        pages = None
        for li in ul.findAll('li'):
            for a in li.findAll('a', href=True):
                pages = int(a['href'][-1])
        if pagenum < pages:
            teams += collectEventTeams(br, link, pagenum+1)
    return teams
