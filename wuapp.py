#! #!/usr/bin/env python3.7
import http.cookiejar as cookielib
import urllib
import mechanize
from mechanize import Browser
from wumodel import *
from topscoreapiservices import *
from topscoreoauth2 import *

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

br.set_handle_equiv(True)
br.set_handle_redirect(True)
br.set_handle_referer(True)
br.set_handle_robots(False)
br.set_handle_refresh(mechanize._http.HTTPRefreshProcessor(), max_time=1)

br.addheaders = [('User-agent', 'Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.1) Gecko/2008071615 Fedora/3.0.1-1.fc9 Firefox/3.0.1')]

Config.set('graphics', 'width', '540')
Config.set('graphics', 'height', '800')

class LoginPage(Screen):
    def verify_login(self, username, password):
        br.open("http://wellington.ultimate.org.nz/")
        br.select_form(action='https://wds.usetopscore.com/signin?original_domain=wellington.ultimate.org.nz')
        br["signin[email]"] = username
        br["signin[password]"] = password
        try:
            br.submit()
        except:
            print("incorrect login")

        if br.response().code == 200:
            self.manager.current = "loading_page"

class LoadingPage(Screen):

    def on_enter(self):
        user = collectUserData()
        self.manager.switch_to(UserPage(user))
        #self.manager.current = "user"

class UserPage(Screen):
    def __init__(self, user, **kwargs):
        super(Screen,self).__init__(**kwargs)
        self.user = user

        self.ids['username'].text = "{} {}".format(self.user.first_name, self.user.last_name)
        self.ids['userimage'].source = self.user.img

class GamePage(Screen):

    def load_game(self, game):
        self.ids["homeTeam"].text = str(game.homeTeam)
        self.ids["awayTeam"].text = str(game.awayTeam)

class TeamPage(Screen):
    pass

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

def collectUserData():
    """br is an authenticated brower"""
    names = []
    img = None
    soup = bs4.BeautifulSoup(br.response().read(), features="html5lib")\

    #Collect users name
    for divtag in soup.findAll('div', recursive=True, class_='global-toolbar-item global-toolbar-item-full global-toolbar-user global-toolbar-item-right'):
        names = divtag.text.split()

    #find redirect link to users page
    for link in br.links(url_regex='^\/u\/{}'.format(names[0].lower())):
        response = br.follow_link(link=next(br.links(url='/u/{}'.format(link.url.split('/')[2]))))
        break
    soup = bs4.BeautifulSoup(response.read(), features="html5lib")

    #webscrape page to collect users info
    for divtag in soup.findAll('div', class_='profile-image'):
        for imgtag in divtag.findAll('img', src=True):
                img = imgtag['src']
    id = collectPlayerID(generateAuthToken(collectUserAPIInfo(br)))

    return User(names[0], names[1], img, id, generateAuthToken(collectUserAPIInfo(br)))

if __name__ == "__main__":
    wuApp().run()
