# Find My Coso SE (Server Edition)

_Find My Coso_ è un progetto di Laurea Triennale finalizzato per la stesura di un elaborato finale (tesi).
Il progetto è comprovato e convalidato dal docente relatore **Fabrizio Romano**, dell'**Università Ca' Foscari di Venezia**.

## Overview del progetto
Lo scopo del progetto è di fornire un servizio di geolocalizzazione da remoto dei propri (e non) dispositivi **Android**.
Dal punto di vista dell'utente finale, _Find My Coso_ è un servizio costituito da due parti:
* Un **sito web**, che permette di effettuare la registrazione dell'utenza, visualizzare lo storico delle posizioni per città, e visualizzare sulla mappa la posizione attuale del dispositivo android. (In corso)
* Un'**applicazione Android**, che permetterà anche'essa la registrazione dell'utenza e l'accesso, consultare lo storico, fornire la posizione attuale (anche quando l'app è in background) e gestire l'uso dei QR (scanner di qr dei dispositivi altrui, dandone però una posizione approssimativa, privacy ndr). (Quasi finita)

## Realizzazione
Verranno utilizzati software come **Android Studio**, **PHPStorm**, e servizi SaaS come **Firebase**.

### Dipendenze
Sono utilizzate le seguenti dipendenze:
* Glide per resize e forme stondate delle foto da visualizzare
* GMS - Google Maps Services
* Dexter per gestione semplificata dei permessi
* EventBus per la comunicazione tra componenti
* RoundedImageView
* QrGenerator + zxing per generazione QR
* Code-Scanner per riconoscimento QR
* Lottie per animazioni .json

## Lato BackEnd - Server Edition
L'applicazione utilizzerà delle REST API basate sul framework Slim 3. 
Il server attualmente è un ThinkPad t431s con Windows 10 64bit, XAMPP aggiornato a Maggio 2020, Apache come web server, MySQL come DBMS e Composer. Tramite le FMC REST API (Find My Coso REST API) l'app android riuscirà a comunicare con il DBMS.


## TODO List
- [ ] Conversione progetto da Firebase a Server privato con FMCREST API
- [ ] Aggiornamento posizione in background
- [ ] Refresh UI in MapsActivity

# 
Sviluppato da Filippo Camoli © 2020.
