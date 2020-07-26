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
from bs4 import BeautifulSoup

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
    response = br.open("https://wds.usetopscore.com/u/auth-key")
    soup = BeautifulSoup(response.read(), features="html5lib")
    table = soup.find("table", class_='table no-border')
    ls = []
    for tr in table.findAll('tr'):
        ls += tr.find('td')
    return [ls[0], ls[1]]

def generateAuthToken(userInfo):
    data = {
        'grant_type': 'client_credentials',
        'client_id' : '{}'.format(userInfo[0]),
        'client_secret' : '{}'.format(userInfo[1])
    }
    req = requests.post(URL, data=data)
    dict = json.loads(req.text)
    return dict["access_token"]

def generateapicsrf():
    nonce = random.randint(1000000000, 9999999999)
    timestamp = time.time()

    message = bytes(CLIENT_ID + str(nonce) + str(timestamp), 'utf-8')
    secret = bytes(CLIENT_SECRET, 'utf-8')
    hm = base64.b64encode(hmac.new(secret, message, digestmod=hashlib.sha256).digest())
    signature = base64.b64encode(bytes(str(nonce) + '|' + str(timestamp) + '|' + str(hm), 'utf-8'))
    print(signature)

def authtest():
    testurl = "https://wds.usetopscore.com/api/games?team_id=274555&active_events_only=true"

    headers = {
        "Authorization" : "Bearer {}".format(generateAuthToken())
    }

    testdata = {
        'auth_token' : 'ID04ab15ead36df0bfeaaff443d30c16a5',
        'api_csrf' : generateapicsrf()
    }

    req = requests.get(testurl, headers=headers, data=testdata)
    print(req.status_code)
    print(req.text)

if __name__ == "__main__":
    authtest()
