import requests
import json
import random
import time
import hashlib
import hmac
import base64

CLIENT_ID = "ID04ab15ead36df0bfeaaff443d30c16a5"
CLIENT_SECRET = "SCA966iqImNOOlIi01zcLLecSi5Z5FzwGy"
URL = "https://wds.usetopscore.com/api/oauth/server"

data = {
    'grant_type': 'client_credentials',
    'client_id' : 'JlDNDIPFEdpO4PbyrAsYOncSTym5QeKY99XpRiSQELeaGE7KXo',
    'client_secret' : 'TEoTshGdvgWLDAcb9teS'
}

def generateAuthToken():
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
