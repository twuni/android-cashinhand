Overview
========

This application uses QR codes and the [Treasury REST API] to receive and make payments with digital cash.

Spend money without worrying about identity theft or credit card fraud. This application acts as the 
"wallet" in a protocol that allows you to conduct worry-free transactions in-person or via the Internet 
by securely trading a token that is worth exactly what you say it's worth and is valid for one-time use. 
Imagine this token as a dollar bill that is worth exactly what you owe. No personal information to divulge, 
no account data to exploit. Just cash in hand.

The money ultimately comes from treasuries. You'll typically get cash from other people or from a digital 
exchange of some sort (this protocol's equivalent of ATMs). In this protocol, anyone can form their own 
treasury, but each treasury has its own merit and worth. It's up to the treasury operator how its currency 
is backed.

Changelog
=========

Version 0.1.0
-------------

* Initial release to the Android market.
* Support for payment/deposit via QR codes.
* Treasury support limited to HTTP, port 8080.
* Treasury [money.twuni.org] exposes /create API call for testing.


[Treasury REST API]: https://sites.google.com/a/twuni.org/digital-currency/treasury/rest-api
[money.twuni.org]: https://money.twuni.org
