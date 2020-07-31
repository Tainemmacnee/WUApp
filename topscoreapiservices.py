import requests
import json
from wumodel import Game
from wumodel import User
from topscoreoauth2 import generateAuthToken
from topscoreoauth2 import collectUserAPIInfo
import bs4

def collectPlayerID(auth_key):
    url = "https://wds.usetopscore.com/api/me"

    headers = {
        "Authorization" : "Bearer {}".format(auth_key)
    }

    req = requests.get(url, headers=headers)
    out = json.loads(req.text)["result"]
    return out[0].get('person_id')

def collectPlayerTeamIDs(playerid, auth_key):
    url = "https://wds.usetopscore.com/api/teams?person_id={0}&active=true".format(playerid)

    headers = {
        "Authorization" : "Bearer {}".format(auth_key)
    }
    req = requests.get(url, headers=headers)
    res = json.loads(req.text)["result"]
    out = []
    for dict in res:
        out.append(dict.get('id'))
    return out

def collectTeamGameInfo(teamid, auth_key):
    """Return list of game objects for each upcoming game for teamid"""
    url = "https://wds.usetopscore.com/api/games?team_id={0}&active_events_only=true".format(teamid)

    headers = {
        "Authorization" : "Bearer {}".format(auth_key)
    }

    req = requests.get(url, headers=headers)
    res = json.loads(req.text)["result"]
    out = []
    for dict in res:
        out.append(Game(dict.get("HomeTeam").get("name"), dict.get("AwayTeam").get("name"), dict.get("start_datetime_tz")))
    return out



if __name__ == "__main__":
    games = []
    # for teamid in collectPlayerTeamIDs(collectPlayerID()):
    #     games += collectTeamGameInfo(teamid)
