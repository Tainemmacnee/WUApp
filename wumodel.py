import sys
import topscoreapiservices as ts

class Game():
    def __init__(self, homeTeam, awayTeam, start_datetime):
        self.homeTeam = homeTeam.encode('utf-8', errors='replace')
        self.awayTeam = awayTeam.encode('utf-8', errors='replace')
        self.start_datetime = start_datetime

    def __str__(self):
        return "Home Team: {0} VS Away Team {1} AT {2}".format(self.homeTeam, self.awayTeam, self.start_datetime)

class Team():
    def __init__(self, name, id, img='https://d36m266ykvepgv.cloudfront.net/uploads/media/66h47QW8vx/s-370-370/uc-logomark-1.png'):
        self.name = name
        self.id = id
        self.img = img


    def __str__(self):
        return "Team: {} with id {}".format(self.name, self.id)

class User():
    def __init__(self, first_name, last_name, img, id, auth_key):
        self.first_name = first_name
        self.last_name = last_name
        self.img = img
        self.id = id
        self.auth_key = auth_key
        self.teams = self.getTeams()

    def getTeams(self):
        return ts.collectPlayerTeams(ts.collectPlayerID(self.auth_key), self.auth_key):

    def __str__(self):
        return "{} {} with id {}".format(self.first_name, self.last_name, self.id)
