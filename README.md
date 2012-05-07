Browser Testing Demo
====================

This code was prepared to demonstrate using Scala/SBT/Selenium to develop in
real time against acceptance tests of browser code.

It was originally written against work code, but that has now been excised.

Instructions
------------

    git clone git@github.com:caoilte/browser_testing_demo.git
    cd browser_testing_demo
    sbt
    it:test

There are only two tests but they require a little setup,
+ A mock Server (to load JSON and images from)
+ A Selenium Driver Server
+ A Selenium enabled browser (Firefox)

The nice thing is that if you run the following again

    it:test

then SBT doesn't need to re-initialise the mock server or
the selenium enabled browser and the tests run very quickly.

Known Problems
--------------

+ If you close the browser the test fixtures don't know how to handle it
+ If you run it:test five or so times you'll get a permgen error
    + This is apparently a common problem with SBT but might also be a
problem with the code, I haven't investigated thoroughly

Conclusions
-----------

+ With a bit of hackery it is possible to re-use a selenium enabled browser
between tests and gain speed boosts large enough to make BDD practical.
+ SBT is potentially a brittle platform for doing TDD. The risks of not running
tests in a new process needs further evaluation