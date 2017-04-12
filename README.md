App Name : NFC Transfer
Contact : gottingoscar@gmail.com
Developers : Oscar Gotting
             Maxime Boguta
             Nicolas Charvoz
             Luca Demmel

App Summary :

This app acts like a NFC business card.
It allows the user to create its own profile
and add multiple personal data to it, such as email
or cellphone number, but also to bind social media accounts
such as Twitter, Facebook etc...
Once the profile is ready, the user touches his phone with one
other phone, and his profile is exchanged with the other through NFC.
He can then import all the data into his contacts.

------  External Libs : ------

- android-recyclerview The famous Android recyclerview which is a more advanced listview
- sdp-android (A cool library that handle smart measurements for views)
- kyleduo.switchbutton (A fork of the Android SwitchButton with more style and functionalities)
- markushi:circlebutton (A circle button with a nice effect when clicked)

------ Milestone 1 - Work ------

Day 1 (spent 1h30)
- Defining the project structure
- Defining the needs, and research about external libraries

Day 2 (spent 4 hours)
- Creation of the App from scratch, adding external libraries
- Initialization of the project structure
- Creation of MainActivity, loading views, implementing listeners
- Creation of AddFieldActivity, loading views, implementing listeners
- Creation of ProfileFragment, loading views, implementing listeners
- Figure out how to implement a vertical ViewPager -> stackoverflow.com
- App Design, creating layouts (xml)
- Implementation of a custom vertical ViewPager (it's more complex than a classic horizontal one)
- Implementation of a custom vertical PagerAdapter for the ViewPager
- Implementation of a RecyclerView for the main fragment

Day 3 (spent 5 hours)
- Implementation of the RecyclerView custom adapter
- Implementation of onCreateContextMenu, onClickListener, OnLongClickListener
inside the RecyclerView's adapters's ViewHolder
- Implementation of the data types (profile fields), abstract classes, enums etc
- Interaction between activites, passing serializable through Intent, startActivityForResult etc...

Day 4 (spent 4 hours)
- Implementation of context menus
- Implementation of a classic ListView
- Implementation of the ListView adapters
- Initialization of drawables, strings (time spent looking for resources on internet)
