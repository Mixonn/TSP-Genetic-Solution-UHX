# TSP Genetic Solution using UHX crossing

My first genetic algorithm. I am using UHX crossing which is explained [here](https://arxiv.org/ftp/arxiv/papers/1504/1504.02590.pdf).
This project contains very simple swing UI (which i will improve soon).
## Getting Started
To run whe algorithm you must declare properties in the genetic.properties file.
* `` max_frame_size`` - maximum window size
* ``population_size`` - number of paths of entire generation (the best results for value between 10-40). 
* ``generation_limit`` - when the generation counter reach the limit - it stops and shows the best result
* ``cross_probab`` - crossing probability; (mostly: value between 0.85-0.95)
* ``mutation_probab`` - mutation probability; (mostly: value between 0.005-0.02)
* ``dataset_path`` - path to the graph
* ``draw_every`` - defines what number of generations the window will be refreshed (0-window will not refresh)
* ``print_every`` - defines what number of generations the best path will be printed to console 
(0 - nothing will be displayed)
* ``print_best_path_on_shutdown`` - true if the best found path should be printed, false if not. The best
path will be printed on the log4j [TRACE];

### Dataset
Dataset should look as follows:  
```` 
Number of vertices  
Index1 coordinateX1 coordinateY1
Index2 coordinateX2 coordinateY2
...
````
Example:
````
3
1 10 10
2 22 41
3 124 1
````
Indexes must be ordered from 1! (I will change it soon).  
In the **resources** directory you can find many instances from [this](http://comopt.ifi.uni-heidelberg.de/software/TSPLIB95/tsp/)
and [this](http://www.math.uwaterloo.ca/tsp/vlsi/index.html) sites.
## Used
* Java 8 (1.8).
* log4j
* Maven
* JUnit with Mockito

## Author

* **Bartosz Osipiuk** - [Mixonn](https://github.com/Mixonn)

## License

This project is licensed under the MIT License - [LICENSE](https://github.com/Mixonn/TSP-Genetic-Solution-UHX/blob/master/LICENSE)

## More
If you got any improvements, ideas - contribute or tell me about it ;)
