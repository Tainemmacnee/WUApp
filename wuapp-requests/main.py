#! #!/usr/bin/env python3.7
import http.cookiejar as cookielib
import urllib
import datetime
import os
import requests
import bs4

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

# cookiejar = cookielib.LWPCookieJar(filename='cookies2.txt')
# if os.path.exists('cookies2.txt'):
#     cookiejar.load()


#br.addheaders = [('User-agent', 'Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.1) Gecko/2008071615 Fedora/3.0.1-1.fc9 Firefox/3.0.1')]

Config.set('graphics', 'width', '540')
Config.set('graphics', 'height', '800')

class LoginPage(Screen):

    def verify_login(self, username, password):

        s = requests.Session()
        LOGIN_URL = 'https://wds.usetopscore.com/signin'
        REQUEST_URL = 'https://wds.usetopscore.com'

        #load cookies
        #s.cookies = cookielib.LWPCookieJar(filename='{}.txt'.format(username))
        # if os.path.exists('{}.txt'.format(username)):
        #     s.cookies.load()
        #     self.manager.current = "loading_page"

        logindata = {
            'signin[xvz32]' : '',
            'signin[email]' : 'tainemmacnee@gmail.com',
            'signin[password]' : 'bedf6Rd!a'
        }

        p = s.post(LOGIN_URL, data=logindata)
        r = s.get(REQUEST_URL)

        soup = bs4.BeautifulSoup(r.text, features="html5lib")


            # if br.response().code == 200:
            #     self.manager.cookiejar.save(ignore_discard=True, ignore_expires=True)
            #     self.manager.current = "loading_page"


class ScreenManagement(ScreenManager):
    def logout(self):
        self.current = "login_page"


class ImageButton(ButtonBehavior, Image):
    pass

class wuapp(App):
    def build(self):
        return Builder.load_file('main.kv')

if __name__ == "__main__":
    wuapp().run()
