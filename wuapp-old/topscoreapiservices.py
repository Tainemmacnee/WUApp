import requests
import json
import wumodel
from topscoreoauth2 import generateAuthToken
from topscoreoauth2 import collectUserAPIInfo
import bs4
import datetime

def collectPlayerID(auth_key):
    url = "https://wds.usetopscore.com/api/me"

    headers = {
        "Authorization" : "Bearer {}".format(auth_key)
    }

    req = requests.get(url, headers=headers)
    out = json.loads(req.text)["result"]
    return out[0].get('person_id')

def collectPlayerTeams(playerid, auth_key):
    url = "https://wds.usetopscore.com/api/teams?person_id={0}&active=true".format(playerid)

    headers = {
        "Authorization" : "Bearer {}".format(auth_key)
    }

    req = requests.get(url, headers=headers)
    res = json.loads(req.text)["result"]
    out = []
    for dict in res:
        name = dict.get('name')
        id = dict.get('id')
        img = dict.get('images').get('200')
        out.append(wumodel.Team(name, id, img))
    return out

def collectTeamEvents(teamid, auth_key):
    url = "https://wds.usetopscore.com/api/events?team_id={}&order_by=date_desc".format(teamid)

    headers = {
        "Authorization" : "Bearer {}".format(auth_key)
    }

    req = requests.get(url, headers=headers)
    res = json.loads(req.text)["result"]
    out = []
    for dict in res:
        name = dict.get('name')
        id = dict.get('id')
        img = dict.get('images').get('200')
        start = dict.get('start')
        out.append(wumodel.Event(id, name, img, start))
    return out

def collectTeamGames(teamid, auth_key):
    """Return list of game objects for each upcoming game for teamid"""
    url = "https://wds.usetopscore.com/api/games?&team_id={}&active_events_only=true&min_date={}".format(teamid,datetime.datetime.now().strftime("%Y/%m/%d"))

    headers = {
        "Authorization" : "Bearer {}".format(auth_key)
    }
    req = requests.get(url, headers=headers)
    res = json.loads(req.text)["result"]
    out = []
    for dict in res:
        #print("{} vs {}".format(dict.get("HomeTeam").get("name"), dict.get("AwayTeam").get("name")))
        out.append(wumodel.Game(dict.get("HomeTeam").get("name"), dict.get("AwayTeam").get("name"), dict.get("start_date"), dict.get("start_time")))
    return out



if __name__ == "__main__":
    games = []
    # for teamid in collectPlayerTeamIDs(collectPlayerID()):
    #     games += collectTeamGameInfo(teamid)
