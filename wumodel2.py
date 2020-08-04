class User():
    def __init__(self, name, events, upcoming_games, img):
        self.name = name
        self.events = events
        self.upcoming_game = upcoming_games
        self.img = img

        self.upsizeimg()

    def upsizeimg(self):
        """img is 30*30 by default but there is a 200*200 version on a similar url."""
        img_temp = self.img[:-6]
        self.img = img_temp+"200.jpg"

    def __str__(self):
        return "User {} with Events {}".format(self.name, self.events)

class Event():
    def __init__(self, name, teams, img):
        self.name = name
        self.teams = teams
        self.img = img

    def __str__(self):
        return "Event {} with Teams {}".format(self.name, self.teams)

class Team():
    def __init__(self, name, female_matchups, male_matchups, img):
        self.name = name
        self.male_matchups = male_matchups
        self.female_matchups = female_matchups
        self.img = img

    def __str__(self):
        return "TEAM {} with members {} {}".format(self.name, self.male_matchups, self.female_matchups)


class TeamMember():
    def __init__(self, name, roles):
        self.name = name
        self.roles = roles

class Game():
    def __init__(self, home_team, away_team, date, time, location, field):
        self.home_team = home_team
        self.away_team = away_team
        self.date = date
        self.time = time
        self.location = location
        self.field = field

    def __str__(self):
        return "GAME {} VS {} ON {} {} AT {} {}".format(self.home_team, self.away_team, self.time, self.date, self.field, self.location)
