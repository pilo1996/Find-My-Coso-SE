# Find My Coso

_Find My Coso_ è un progetto di Laurea Triennale finalizzato per la stesura di un elaborato finale (tesi).
Il progetto è comprovato e convalidato dal docente relatore **Fabrizio Romano**, dell'**Università Ca' Foscari di Venezia**.

## Overview del progetto
Lo scopo del progetto è di fornire un servizio di geolocalizzazione da remoto dei propri (e non) dispositivi **Android**.
Dal punto di vista dell'utente finale, _Find My Coso_ è un servizio costituito da due parti:
* Un **sito web**, che permette di effettuare la registrazione dell'utenza, visualizzare lo storico delle posizioni per città, e visualizzare sulla mappa la posizione attuale del dispositivo android. (TODO)
* Un'**applicazione Android**, che permetterà anche'essa la registrazione dell'utenza e l'accesso, consultare lo storico, fornire la posizione attuale (anche quando l'app è in background) e gestire l'uso dei QR (scanner di qr dei dispositivi altrui, dandone però una posizione approssimativa, privacy ndr). (Quasi finita)

## Realizzazione
Verranno utilizzati software come **Android Studio**, **PHPStorm**, e servizi SaaS come **Firebase**.

### Dipendenze
Sono utilizzate le seguenti dipendenze:
* Firebase analytics, auth, storage e database
* Glide per resize e forme stondate delle foto da visualizzare
* GMS - Google Maps Services
* Dexter per gestione semplificata dei permessi
* EventBus per la comunicazione tra componenti
* RoundedImageView
* QrGenerator + zxing per generazione QR
* Code-Scanner per riconoscimento QR
* Lottie per animazioni .json

## Lato BackEnd
La prima parte del progetto verrà realizzata con le tecnologie automatizzare proposte da Google Firebase.
Grazie all'utilizzo di Firebase, verranno semplificate di molto la gestione del database, notifiche, dati e mail, evitando l'impostazione di un server fisico fatto in casa.
La seconda parte del progetto consisterà nel sostituire Firebase con un server casalingo. (TODO)

## TODO List
- [ ] Aggiornamento posizione in background
- [ ] Refresh UI in MapsActivity

# 
Sviluppato da Filippo Camoli © 2020.
