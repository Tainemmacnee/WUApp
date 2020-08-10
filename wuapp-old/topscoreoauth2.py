import requests
import json
import random
import time
import hashlib
import hmac
import base64

import http.cookiejar as cookielib
import urllib
from mechanize import Browser
import bs4

CLIENT_ID = ""
CLIENT_SECRET = ""
URL = "https://wds.usetopscore.com/api/oauth/server"

data = {
    'grant_type': 'client_credentials',
    'client_id' : '',
    'client_secret' : ''
}

def collectUserAPIInfo(br):
    """br is an authenticated brower"""
    response = br.open("https://wds.usetopscore.com/u/oauth-key")
    soup = bs4.BeautifulSoup(response.read(), features="html5lib")
    table = soup.find("table", class_='table no-border')
    ls = []
    for tr in table.findAll('tr'):
        ls += tr.find('td')
    return [ls[0], ls[1]]

def generateAuthToken(userInfo):
    data = {
        'grant_type': 'client_credentials',
        'client_id' : "{}".format(userInfo[0]),
        'client_secret' : "{}".format(userInfo[1])
    }
    req = requests.post(URL, data=data)
    dict = json.loads(req.text)
    return dict["access_token"]

def authtest():
    testurl = "https://wds.usetopscore.com/api/games?team_id=274555&active_events_only=true"

    headers = {
        "Authorization" : "Bearer {}".format(generateAuthToken())
    }

    testdata = {
        'auth_token' : 'ID04ab15ead36df0bfeaaff443d30c16a5'
    }

    req = requests.get(testurl, headers=headers, data=testdata)
    print(req.status_code)
    print(req.text)

if __name__ == "__main__":
    loginSelf()
