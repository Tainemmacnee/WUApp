import sys
import topscoreapiservices as ts
import datetime

class Game():
    def __init__(self, homeTeam, awayTeam, start_date, start_time):
        self.homeTeam = homeTeam.encode('utf-8', errors='replace')
        self.awayTeam = awayTeam.encode('utf-8', errors='replace')
        dt = start_date + start_time
        #print("date: {} time: {} DT: {}".format(start_date, start_time, dt))
        self.datetime = datetime.datetime.strptime(dt, "%Y-%m-%d%H:%M:%S")

    def __str__(self):
        return "Home Team: {0} VS Away Team {1} AT {2}".format(self.homeTeam, self.awayTeam, self.datetime.strftime('%Y/%m/%d, %H:%M:%S'))

class Team():
    def __init__(self, name, id, img='https://d36m266ykvepgv.cloudfront.net/uploads/media/66h47QW8vx/s-370-370/uc-logomark-1.png'):
        self.name = name
        self.id = id
        self.img = img

    def getEvents(self, auth_key):
        return ts.collectTeamEvents(self.id, auth_key)

    def getUpcomingGames(self, auth_key):
        return ts.collectTeamGames(self.id, auth_key)

    def __str__(self):
        return "Team: {} with id {}".format(self.name, self.id)

class Event():
    def __init__(self, id, name, img, start):
        self.id = id
        self.name = name
        self.img = img
        self.start = start

    def __str__(self):
        return "Game: {} with id {}".format(self.name, self.id)

class User():
    def __init__(self, first_name, last_name, img, id, auth_key):
        self.first_name = first_name
        self.last_name = last_name
        self.img = img
        self.id = id
        self.auth_key = auth_key
        self.teams = None
        self.events = None
        self.upcomingGames = None

        #for game in self.getUpcomingGames():
            #print(game)

        # for event in self.getEvents():
        #     print(event.name)

    def getEvents(self):
        if self.events == None:
            events = []
            for team in self.getTeams():
                events += team.getEvents(self.auth_key)
            self.events = events
        return sorted(self.events, key=lambda x: datetime.datetime.strptime(x.start, '%Y-%m-%d'), reverse=True)

    def getTeams(self):
        if self.teams == None:
            self.teams = ts.collectPlayerTeams(ts.collectPlayerID(self.auth_key), self.auth_key)
        return self.teams

    def getUpcomingGames(self):
        if self.upcomingGames == None:
            games = []
            for team in self.getTeams():
                games += team.getUpcomingGames(self.auth_key)
            self.upcomingGames = games
        return sorted(self.upcomingGames, key=lambda x: x.datetime, reverse=True)

    def __str__(self):
        return "{} {} with id {}".format(self.first_name, self.last_name, self.id)
