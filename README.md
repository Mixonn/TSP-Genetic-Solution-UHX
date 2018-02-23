# TSP Genetic Solution using UHX crossing

My first genetic algorithm. I am using UHX crossing which is explained [here](https://arxiv.org/ftp/arxiv/papers/1504/1504.02590.pdf).
This project contains very simple swing interface (which i will improve soon).
## Getting Started
To run whe algorithm you must declare some final variables which can be find in Main class:
* `` maxFrameSize`` - maximum window size
* ``POPULATION_SIZE`` - value between 10-40 (the best results). 
* ``GENERATION_LIMIT`` - when the generation counter reach the limit - it stops and shows the best result
* ``CROSS_PROBAB`` - crossing probability; value between 0.85-0.95
* ``MUTATION_PROBAB`` - mutation probability; value between 0.005-0.02
* ``DATASET`` - your dataset path
* ``DRAW_EVERY`` - defines what number of generations the window will be refreshed (0-window will now refresh)
* ``PRINT_EVERY`` - defines what number of generations the best path will be printed to console;

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
## You need
* Java 8 (1.8).

## Author

* **Bartosz Osipiuk** - [Mixonn](https://github.com/Mixonn)

## License

This project is licensed under the MIT License - [LICENSE](https://github.com/Mixonn/TSP-Genetic-Solution-UHX/blob/master/LICENSE)

## More
If you got any improvements, ideas - contribute or tell me about it ;)
