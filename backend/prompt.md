Lorsque tu fini, fais moi un fichier search-service-test.http pour tester le bail et un bon readme pour comprendre comment ca marche.

J'ai deja forward le elastic search sur le port localhost 9200. Pour la securite aussi utilise toujours la dep oauth2 et non jjwt. Et note qu'il exist un api gateway donc pas besooin de politique de cors.
J'ai aussi port-forward tout les autres svc donc tu pourras bien les utilise au besoin