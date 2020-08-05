#! #!/usr/bin/env python3.7
import http.cookiejar as cookielib
import urllib
import mechanize
from mechanize import Browser
from wumodel import *
from topscoreapiservices import *
from topscoreoauth2 import *
from wuwebscrape import *
import datetime
import os

from kivy.uix.screenmanager import ScreenManager, Screen
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.gridlayout import GridLayout
from kivy.metrics import dp
from kivy.app import App
from kivy.lang import Builder
from kivy.uix.button import Button
from kivy.uix.recycleview import RecycleView
from kivy.uix.recycleview.views import RecycleDataViewBehavior
from kivy.uix.label import Label
from kivy.properties import BooleanProperty
from kivy.uix.recycleboxlayout import RecycleBoxLayout
from kivy.uix.behaviors import FocusBehavior
from kivy.uix.recycleview.layout import LayoutSelectionBehavior
from kivy.config import Config
from kivy.clock import Clock
from kivy.loader import Loader
from kivy.uix.image import Image
from kivy.uix.behaviors import ButtonBehavior
from kivy.uix.accordion import Accordion

t1 = datetime.datetime.now()
# cookiejar = cookielib.LWPCookieJar(filename='cookies2.txt')
# if os.path.exists('cookies2.txt'):
#     cookiejar.load()

# br = Browser()
# br.set_cookiejar( cookiejar )
#
# br.set_handle_equiv(True)
# br.set_handle_redirect(True)
# br.set_handle_referer(True)
# br.set_handle_robots(False)
# br.set_handle_refresh(mechanize._http.HTTPRefreshProcessor(), max_time=1)

#br.addheaders = [('User-agent', 'Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.1) Gecko/2008071615 Fedora/3.0.1-1.fc9 Firefox/3.0.1')]

Config.set('graphics', 'width', '540')
Config.set('graphics', 'height', '800')

class LoginPage(Screen):

    def verify_login(self, username, password):

        self.manager.cookiejar = cookielib.LWPCookieJar(filename='{}.txt'.format(username))

        if os.path.exists('{}.txt'.format(username)):
            self.manager.cookiejar.load()
            self.manager.current = "loading_page"
        else:
            br = getBrowser(self.manager.cookiejar)

            br.open("http://wds.usetopscore.com/")
            br.select_form(action='https://wds.usetopscore.com/signin')
            br["signin[email]"] = username
            br["signin[password]"] = password
            try:
                br.submit()
            except:
                print("incorrect login")

            if br.response().code == 200:
                self.manager.cookiejar.save(ignore_discard=True, ignore_expires=True)
                self.manager.current = "loading_page"

class LoadingPage(Screen):

    def on_enter(self):
        user = collectUserData(getBrowser(self.manager.cookiejar))
        print("TOTAL LOADING TIME: {}".format(datetime.datetime.now()-t1))
        self.manager.switch_to(UserPage(user))

class UserPage(Screen):
    def __init__(self, user, **kwargs):
        super(Screen,self).__init__(**kwargs)
        self.user = user

        self.ids['username'].text = "{}".format(self.user.name)
        self.ids['userimage'].source = self.user.img

        data = []
        for game in user.upcoming_games:
            data.append(game.toDataDict())
        self.ids['upg'].data = data

        data = []
        for team in user.getTeams():
            data.append(team.toDataDict())
        self.ids['teams'].data = data

class GamePage(Screen):

    def load_game(self, game):
        self.ids["homeTeam"].text = str(game.homeTeam)
        self.ids["awayTeam"].text = str(game.awayTeam)

class TeamPage(Screen):
    pass

class ScreenManagement(ScreenManager):
    cookiejar = None
    def logout(self):
        self.current = "login_page"
        self.manager.cookiejar = None

class Game_RV(RecycleView):
    def __init__(self, **kwargs):
        super(Game_RV, self).__init__(**kwargs)

        self.data = [{'home_source' : 'https://d36m266ykvepgv.cloudfront.net/uploads/media/TpbNfwbJFz/c-40-40/11132abf624399ef34b9336181e6fa3b-1.jpg',
                        'home_name' : 'Dumbo', 'away_source': 'https://d36m266ykvepgv.cloudfront.net/uploads/media/TpbNfwbJFz/c-40-40/11132abf624399ef34b9336181e6fa3b-1.jpg',
                        'away_name': 'BURNEDO', 'loc_text' : 'Tue, 4/08/2020 ASB Sports Center #2'}]

class ComplexGameButtonItem(ButtonBehavior, RecycleDataViewBehavior, GridLayout):

    index = None
    cols = 1

    def refresh_view_attrs(self, rv, index, data):
        ''' Catch and handle the view changes '''
        self.index = index
        return super(ComplexGameButtonItem, self).refresh_view_attrs(rv, index, data)

    def printer(self):
        print("YEET")

class Team_RV(RecycleView):
    def __init__(self, **kwargs):
        super(Team_RV, self).__init__(**kwargs)

        self.data = [{'team_source' : 'https://d36m266ykvepgv.cloudfront.net/uploads/media/TpbNfwbJFz/c-40-40/11132abf624399ef34b9336181e6fa3b-1.jpg',
                        'team_name' : 'Dumbo'}]

class ComplexTeamButtonItem(ButtonBehavior, RecycleDataViewBehavior, GridLayout):

    index = None
    cols = 1

    def refresh_view_attrs(self, rv, index, data):
        ''' Catch and handle the view changes '''
        self.index = index
        return super(ComplexTeamButtonItem, self).refresh_view_attrs(rv, index, data)

    def printer(self):
        print("YEET")

class Accor(Accordion):
    pass



class ImageButton(ButtonBehavior, Image):
    pass

kv_file = Builder.load_file('wukv.kv')
class wuApp(App):
    def build(self):
        return kv_file



if __name__ == "__main__":
    wuApp().run()
