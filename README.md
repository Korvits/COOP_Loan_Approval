# Laenu kinnitamise süsteem (COOP tarkvaraarendaja praktika)

See projekt on COOP tarkvaraarendaja praktika proovitööks loodud laenutaotluste haldamise ja kinnitamise süsteem. Rakendus võimaldab esitada uusi laenutaotlusi, vaadata nende staatust, genereerida maksegraafikuid ning taotlusi kinnitada või tagasi lükata.

## Juurdepääs rakendusele

Jooksutada käsk 
```bash
docker compose up
```

Kui Dockeri konteinerid on käivitatud, saate rakenduse erinevatele osadele juurde pääseda järgmiste kohalike URL-ide abil:
* **Frontend UI (Kasutajaliides):** [http://localhost](http://localhost)
* **REST API Endpoint:** [http://localhost/api/v1/loan-applications](http://localhost/api/v1/loan-applications)
* **Swagger API Documentation:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
