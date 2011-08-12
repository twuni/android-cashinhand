Overview
========

Cash in Hand lets you buy stuff online or in-person without revealing your credit
card information or identity. In fact, Cash in Hand doesn't care about any of that.
It operates on Digital Cash, which is useful in a variety of scenarios.

You know, like when you're buying that sofa from that shady person on Craigslist.

Or when you're donating some beer money to an independent game developer
because you liked his game.

Or when you want to set up an online shop but it's too costly and too much of a
hassle to accept credit cards.

Or when you've saved up enough money to make a down payment on a car and you want
to get a great deal because you have cash in hand, but you don't feel safe carrying
around that much money.

Okay, so what is Digital Cash?
==============================

It's a way of safely, securely, and anonymously transferring money from one person
to another. It is meant to simulate the real-life scenario of handing someone exact
change to pay for something.

An essential component of Digital Cash is the issuer -- the Digital Cash equivalent
of the Federal Reserve Bank. An issuer must be able to prove that it operates fairly
and in a manner that prevents and/or protects against criminal activity. Cash is 
accepted in trades only when both parties involved trust that the cash's issuer is
fairly operated and that the cash was not acquired through unlawful, exploitative,
or nefarious means.

You get Digital Cash via one of three methods:

 1. Directly from an issuer, by purchasing Digital Cash tokens.
    This is like getting U.S. dollars directly from the Federal Reserve Bank.

 2. As payment from another person.
    This is like when you sell a concert ticket to someone on Craigslist, or
    when a friend hands you a $5 bill.
    This is the most likely way you will receive a Digital Cash token.

 3. From yourself, by operating your own currency.
    This is like printing your own money and expecting other people to accept it.


Developing
==========

The following properties must be configured in your `~/.m2/settings.xml` for the project
to build properly:

 * `android.sdk.path`: The path to the Android SDK on your filesystem.
 * `android.sdk.platform`: The version of the Android OS to target (not the API level).
 * `android.keystore`: The path to the keystore to use to sign the application.
 * `android.keystore.password`: The password to the keystore.
 * `android.keystore.key`: The alias of the key within the keystore to use to sign the application.
 * `android.keystore.key.password`: The password to the key itself within the keystore.
