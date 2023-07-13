![benoit-vincent-go4lunch](https://github.com/benlinux1/017_BenoitVINCENT-Go4Lunch/assets/78255467/833d094e-ebc7-40d5-ba65-1d940223934f)![android-app-100_-java](https://github.com/benlinux1/017_BenoitVINCENT-Go4Lunch/assets/78255467/2d14262f-e3b8-4161-9a3e-4b44723c9ff9)

# VERSION FRANCAISE
(English translation in the 2nd part of the documentation below)


# DA_ANDROID_GO4LUNCH
Ce dépôt contient une application intitulée **Go4Lunch**.

Il s'agissait ici de concevoir une **application permettant d'informer et de rassembler l'ensemble des collaborateurs
dans différents lieux de restauration pour le déjeuner**.


## Technologies
**100% JAVA**, ce projet a été réalisé à partir de l'IDE Android Studio.


## Fonctionnalités

Cette applcation vous permet de/d' :

- Créer votre compte utilisateur via votre compte Google, Facebook, Twitter, ou à partir de votre adresse e-mail
- Afficher les restaurants situés autour de vous sur une carte, ou sous forme de liste
- Rechercher un restaurant dans la barre de recherche, via son nom et/ou son adresse
- Consulter la fiche détaillée d'un restaurant (adresse, note, n° de téléphone, site web...)
- Contacter un restaurant par téléphone, et/ou visiter son site internet
- Modifier votre compte (nom d'utilisateur, avatar...)
- Supprimer votre compte utilisateur
- Effectuer une "réservation" virtuelle dans le restaurant de votre choix, à la date de votre choix
- Indiquer aux utilisateurs de l'application dans quel restaurant vous déjeunez aujourd'hui
- Consulter la liste des utilisateurs de l'application et voir dans quel restaurant ils déjeunent aujourd'hui
- Visualiser la liste de toutes vos réservations virtuelles
- Supprimer une réservation de votre liste générale
- Recevoir une notification tous les jours à 12:00, vous rappelant dans quel restaurant vous déjeunez

![Fonctionnalités FR](https://user-images.githubusercontent.com/78255467/198533983-fc5c6015-c1d5-428c-885e-1191edf1f661.png)

![Fonctionnalités FR 2](https://user-images.githubusercontent.com/78255467/198538930-c12981b0-80f7-41cd-8d63-18e6ea00742b.png)


## Tests

L'application a été testée par émulation sur les appareils suivants :
- Pixel 4 (API 30)
- Pixel 5X (API 30)

Elle a également été testée dans des conditions réelles, sur un smartphone physique Samsung Galaxy S20 et sur une tablette Galaxy A7.


## Langues

L'application est entièrement traduite en Anglais et en Français, selon la langue de l'appareil que vous utilisez.


## Bibliothèques / API

Cette application utilise les bibliothèques / API suivantes :
- Material Design
- Firebase (UI, Firestore Database, Storage)
- Facebook SDK
- Twitter API V2
- Glide
- Easy Permissions
- Google Map
- Google Places (nearbySearch, autoComplete)
- JUnit
- Mockito


## Installation

- Ouvrez votre IDE préféré (ici Android Studio)
- Ouvrez un nouveau projet et choississez "Get From Version Control"
- Clonez l'application en copiant / collant ce lien : https://github.com/benlinux1/017_BenoitVINCENT-Go4Lunch.git
- Spécifiez un dossier de destination sur votre ordinateur et cliquez sur "clone"
- Voici les étapes en images :

![GIT](https://user-images.githubusercontent.com/78255467/198230597-028f6051-8cff-4e73-9787-f6aff219503e.png)

- L'application va ensuite se compiler dans Android Studio


## Utilisation / Lancement de l'application

Vous pouvez utiliser cette application via un émulateur, pour une simulation sur le mobile ou la tablette de votre choix.
Pour ce faire, rendez-vous dans le ruban à droite de la fenêtre puis "create device" et laissez-vous guider.

Vous pouvez également utiliser cette application sur votre smartphone personnel en choississant l'option "physical"
Ensuite, lancez l'application en cliquant sur le bouton triangulaire "Run", comme ci-dessous :

![Sans titre-2](https://user-images.githubusercontent.com/78255467/163193524-89842086-ca39-475c-afc2-e39e3e586f68.png)


## Contribuez au projet

Go4lunch est un projet open source. Vous pouvez donc en utiliser le code source à votre guise pour développer vos propres fonctionnalités.


## Auteurs

Notre équipe : BenLinux & FranckBlack


==========================================================================================
==========================================================================================


![benoit-vincent-go4lunch](https://github.com/benlinux1/017_BenoitVINCENT-Go4Lunch/assets/78255467/833d094e-ebc7-40d5-ba65-1d940223934f)![android-app-100_-java](https://github.com/benlinux1/017_BenoitVINCENT-Go4Lunch/assets/78255467/2d14262f-e3b8-4161-9a3e-4b44723c9ff9)

# ENGLISH VERSION
(version française en 1ère partie de la documentation)


# Go4Lunch

This repository contains an Android app untitled **Go4Lunch**.

The aim here was to design an **application to inform and bring together all employees in different restaurants for lunch**.


## Technologies

**100% JAVA**, this project was made with Android Studio IDE.


## Features

This app allow you to :

- Create your user account from Google, Facebook, Twitter login, or with your own email address
- Display restaurants around you on a Google Map, or as a list
- Search restaurants in the search bar, by name or by address
- Consult detailed sheet of restaurants (address, rating, phone number, website)
- Contact a restaurant by phone, and/or visit its website
- Update your account (username, avatar...)
- Delete your account
- Make virtual booking in a chosen restaurant, at a chosen date
- Show to app's users in which restaurant you take a lunch today
- Display app's users list and see in which restaurant each of them take a lunch today
- Visualize your bookings list, sorted by date
- Delete chosen bookings from your bookings list
- Receive a notification, each day at 12am, that remember you your restaurant of the day

![Fonctionnalités GB](https://user-images.githubusercontent.com/78255467/198534046-c6080be6-f68c-4166-989c-aa66214b9f6f.png)

![Fonctionnalités GB 2](https://user-images.githubusercontent.com/78255467/198538573-c51c5752-ff77-4289-8936-4bda45a4f207.png)


## Tests

This app has been tested on following devices :
- Pixel 4 (API 30) with Google Play services
- Pixel 5X (API 30) with Google Play services

It was also tested in real conditions, on a physical phone Samsung Galaxy S20 and Galaxy Tab A7.


## Languages

Go4lunch is fully available in English and in French languages, automatically set according to your device's general language.


## Libraries / API

This app uses the following libraries / API :
- Material Design
- Firebase (UI, Firestore Database, Storage)
- Facebook SDK (for login)
- Twitter API V2 (for Login)
- Glide
- Easy Permissions
- Google Map
- Google Places (nearbySearch, autoComplete)
- JUnit
- Mockito


## Install
- Open your favorite IDE (here Android Studio)
- Open a new project and choose "Get From Version Control" option
- Clone the app by copying / pasting this repository link : https://github.com/benlinux1/017_BenoitVINCENT-Go4Lunch.git
- Specify a destination folder name on your computer and click "clone"
- Here are the steps to illustrate in pictures :

![GIT](https://user-images.githubusercontent.com/78255467/198230597-028f6051-8cff-4e73-9787-f6aff219503e.png)

- Next, the app will compile in Android Studio


## Use / How to launch app

You can use this app with an emulator, for a chosen mobile simulation.
To do that, go to the tools ribbon in the right part of the window and click "create device". Then, follow the instructions on screen.

You can also use this app with your own smartphone, choosing "physical" option in device manager,
Then, launch app clicking on the green triangle button "Run", like in this example :

![Sans titre-2](https://user-images.githubusercontent.com/78255467/163193524-89842086-ca39-475c-afc2-e39e3e586f68.png)


## Contribute to the project

Go4lunch is an open source project. Feel free to fork the source and contribute with your own features.


## Authors

Our code squad : BenLinux & FranckBlack
