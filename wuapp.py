#! #!/usr/bin/env python3.7
import http.cookiejar as cookielib
import urllib
from mechanize import Browser
from wumodel import Game
from topscoreapiservices import *

import concurrent.futures
from functools import partial
import multiprocessing as mp

from kivy.uix.screenmanager import ScreenManager, Screen
from kivy.uix.boxlayout import BoxLayout
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

br = Browser()
cookiejar = cookielib.LWPCookieJar()
br.set_cookiejar( cookiejar )

Config.set('graphics', 'width', '540')
Config.set('graphics', 'height', '800')

class LoginPage(Screen):
    def verify_login(self, username, password):
        if username == "a" and password == "a":
            self.manager.current = "loading_page"
            return

        br.open("http://wellington.ultimate.org.nz/")
        br.select_form(action='https://wds.usetopscore.com/signin?original_domain=wellington.ultimate.org.nz')
        br["signin[email]"] = username
        br["signin[password]"] = password
        try:
            br.submit()
        except:
            print("incorrect login")

        if br.response().code == 200:
            self.manager.current = "user"

class UserPage(Screen):
    pass

class GamePage(Screen):

    def load_game(self, game):
        self.ids["homeTeam"].text = str(game.homeTeam)
        self.ids["awayTeam"].text = str(game.awayTeam)

class LoadingPage(Screen):

    def on_enter(self):
        games = []
        for teamid in collectPlayerTeamIDs(collectPlayerID()):
             games += collectTeamGameInfo(teamid)

        #self.manager.get_screen("user").children[0].children[1].data = [{'text': str(game), 'game':game} for game in games]
        self.manager.current = "user"

#test edit
class ScreenManagement(ScreenManager):
    def logout(self):
        self.current = "login_page"
        cookiejar.clear()

class RV(RecycleView):
    pass

class gameSelectButton(Button):
    pass

class ImageButton(ButtonBehavior, Image):
    pass

kv_file = Builder.load_file('wukv.kv')
class wuApp(App):
    def build(self):
        return kv_file

wuApp().run()
