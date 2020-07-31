import sys
class Game():
    def __init__(self, homeTeam, awayTeam, start_datetime):
        self.homeTeam = homeTeam.encode('utf-8', errors='replace')
        self.awayTeam = awayTeam.encode('utf-8', errors='replace')
        self.start_datetime = start_datetime

    def __str__(self):
        return "Home Team: {0} VS Away Team {1} AT {2}".format(self.homeTeam, self.awayTeam, self.start_datetime)

class User():
    def __init__(self, first_name, last_name, img, id):
        self.first_name = first_name
        self.last_name = last_name
        self.img = img
        self.id = id

    def __str__(self):
        return "{} {} with id {}".format(self.first_name, self.last_name, self.id)
