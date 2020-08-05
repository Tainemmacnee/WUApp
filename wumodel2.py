class User():
    def __init__(self, name, events, upcoming_games, img):
        self.name = name
        self.events = events
        self.upcoming_games = upcoming_games
        self.img = img

        self.upsizeimg()

    def upsizeimg(self):
        """img is 30*30 by default but there is a 200*200 version on a similar url."""
        img_temp = self.img[:-6]
        self.img = img_temp+"200.jpg"

    def getTeams(self):
        teams = []
        for event in self.events:
            teams += event.getTeamsWithPlayer(self.name)
        return teams

    def __str__(self):
        return "User {} with Events {}".format(self.name, self.events)

class Event():
    def __init__(self, name, teams, img):
        self.name = name
        self.teams = teams
        self.img = img

    def __str__(self):
        return "Event {} with Teams {}".format(self.name, self.teams)

    def getTeamsWithPlayer(self, player):
        teams = []
        for team in self.teams:
            if team.hasPlayer(player):
                teams.append(team)
        return teams

class Team():
    def __init__(self, name, female_matchups, male_matchups, img):
        self.name = name
        self.male_matchups = male_matchups
        self.female_matchups = female_matchups
        self.img = img

    def hasPlayer(self, player):
        for team_member in self.female_matchups:
            if player == team_member:
                return True
        for team_member in self.male_matchups:
            if player == team_member:
                return True
        return False

    def __str__(self):
        return "TEAM {} with members {} {}".format(self.name, self.male_matchups, self.female_matchups)

    def toDataDict(self):
        thisdict = {}
        thisdict['team_source'] = self.img
        thisdict['team_name'] = self.name
        return thisdict

class TeamMember():
    def __init__(self, name, roles):
        self.name = name
        self.roles = roles

class Game():
    def __init__(self, home_team, away_team, home_team_img, away_team_img, date, time, location, field):
        self.home_team = home_team
        self.away_team = away_team
        self.date = date
        self.time = time
        self.location = location
        self.field = field
        self.home_team_img = home_team_img
        self.away_team_img = away_team_img

    def __str__(self):
        return "GAME {} VS {} ON {} {} AT {} {}".format(self.home_team, self.away_team, self.time, self.date, self.field, self.location)

    def toDataDict(self):
        thisdict = {}
        thisdict['home_source'] = self.home_team_img
        thisdict['home_name'] = self.home_team
        thisdict['away_source'] = self.away_team_img
        thisdict['away_name'] = self.away_team
        thisdict['loc_text'] = "{} {} {} {}".format(self.time, self.date, self.field, self.location)
        return thisdict
