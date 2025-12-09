## Hvad der er blevet gjort i projektet

Flock Behavior er blevet rykket ud fra Boid sammen med alle metoder som blev brugt af denne.

Der er tilføjet endnu en BehaviorStrategy, ForageBehavior, der udvider på Flockbehavior ved at bruge calculateForces fra 
FlockBehavior og tilføje ekstra til separation baseret på afstand til nærmeste food.

Der er tilføjet to sliders til UI til opdatering af hvor meget mad der bliver tilføjet til kortet og hvor ofte.


Der er lavet microbenchmarks af de fire spacial indexes:

Der blevet valgt følgende parametre:

Antal boids: 2000
  - Nok til at gøre en forskel på tid mellem naiv og de andre algoritmer (Baseret på observation med UI)
  - Tager ikke langt tid at køre en simulation

Iterations: 500
  - Nok til at give et ordenligt gennemsnit

Warmup: 50
  - Bør være nok, ved det egentlig ikke.

Radius: 20, 60 og 100:
  - Lidt af hvert for at se hvordan det påvirker de forskellige algoritmer

## Benchmarks

Interessant at Naive bliver mindre effektiv når kun søgeradius stiger når den kigge alle Boids igennem hver gang.
Der kan ses med når søge radius nærmer sig størrelsen på canvas så er der færre boids der udelukkes af de optimeret 
algoritmer så kommer tiden tættere på den naive. 


Benchmark med 2000 boids, radius = 20.0  
Warmup: 50 iterationer  
Måling: 500 iterationer  

NaiveSpatialIndex   :	8,1161 ms per iteration  
KDTreeSpatialIndex  :	1,3657 ms per iteration  
QuadTreeSpatialIndex:	0,6804 ms per iteration  
SpatialHashIndex    :	2,3941 ms per iteration  
---
Benchmark med 2000 boids, radius = 60.0  
Warmup: 50 iterationer  
Måling: 500 iterationer  

NaiveSpatialIndex   :	13,0585 ms per iteration  
KDTreeSpatialIndex  :	5,7313 ms per iteration  
QuadTreeSpatialIndex:	4,1783 ms per iteration  
SpatialHashIndex    :	12,9977 ms per iteration
---
Benchmark med 2000 boids, radius = 100.0  
Warmup: 50 iterationer  
Måling: 500 iterationer  

NaiveSpatialIndex   :	16,8572 ms per iteration  
KDTreeSpatialIndex  :	9,8993 ms per iteration  
QuadTreeSpatialIndex:	6,3600 ms per iteration  
SpatialHashIndex    :	16,0704 ms per iteration  
