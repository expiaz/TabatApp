TabatApp par Rémi Gidon
Application réalisée sous l'api 25 Nougat (Android 7.1.1 google APIs)
Les tests pour le design ont étés réalisés avec AVD Nexus 5X (1080x1920 420dpi)

L'application se compose de 3 vues, dont une utilisée pour 2 actions.
- MainActivity: vue de listing avec actions pour ajouter, éditer, supprimer ou jouer un training
- EditTrainingActivity: vue d'édition (ou d'ajout si appelée vide) d'un training
- PlayTrainingActivity: vue de jeu d'un training, utilisant un fragment pour le compteur

Chaque activité est lancée depuis la main et y revient lorsque terminée.
De plus chacune s'occupe des enregistrements en base de données avant sa destruction, cela
permet à la main de se re-hydrater à partir de la base de données dans tout les cas.

Pour faciliter les calculs sur un training, celui-ci est découpé en parties, qui ne sont
pas moins que la préparation avant un tabata, le travail pendant un cycle et le repos pendant
un cycle. Chaque partie dispose ainsi d'un nom, d'un id, d'une couleur attribuée et
d'un temps total. Une liste chaînée des parties est construite, permettant d'itérer dessus avec le timer,
et ne plus voir un training comme une répétition de parties mais une suite de celles-ci.
La formule pour calculer les parties est la suivante :
- nombre de tabatas * nombres de cycles * 2

L'activité play utilise un fragment pour le timer, qui manage sa vue via un listener et des
méthodes appelées respectivement lors du démarrage d'une partie, d'un tick de l'horloge et
de la fin du training.

La seule entité stockée et managée par SugarORM est un Training, qui contient toutes les informations
sous forme dénormalisée, permettant un modèle simple.

La seule activité utilisant deux layouts pour les modes landscape et portrait est celle du jeu,
l'écran étant découpé en deux entre le compteur et les informations.

(le fichier howto.md est un pense bete personnel)

PS: il se peut qu'il y est un nombre de fautes important dans les commentaires et dans
ce même fichier.