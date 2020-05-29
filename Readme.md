# Yamm - A coolaborative savings app

## About
This app is for people who have a shared pool of money, and want to see for how long they can go
without being bankrupt. You can also add your expenses and incomes in categories, and watch nice
charts about your spending habits.

## Technical details
This project follows clean architecture, accompanied by Test Driven Developement.

Specifically to Android, I use Firestore for the remote, and Room for the local databases. These
reside in the `Remote` and `Local` modules. I opted out of using Firestore's data synchronisation
algorithm as I want to preserve the freedom to implement the backend myself. For this reason, I have
a `Data` module where the synchronisation algorithm resides.

For syncrhonisation purposes I use timestamps, before and after every operation I synchronise
the remote and local datastores.

In the presentation layer, defined in the `Presentation` module I use MvRx for easy use of the
ModelViewIntent design pattern, which makes it easier to develop in a reactive style.

In the `MobileUI` layer I provide the concrete implementation for the cross module dependencies
using Koin as the DI framework. I choose Koin over dagger, as it is more idiomatic kotlin, and
it's reflection-based DI makes compile-times considerably faster.

The `Domain` layer is the center of the architecture, here is where my usecases are defined.

## How to build it yourself
Just import the project in android studio, and click the start button to see the app in action.
To run all the unit tests in every module run: `gradlew test`
