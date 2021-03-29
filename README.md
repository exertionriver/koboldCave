# koboldCave
Demonstration of probabilistically-generated oakLeaf and nodeMesh using Korge Multiplatform Kotlin Game Engine

Built using IntelliJ Community Edition 2020.3.2 / Kotlin Plugin 1.4.31 on Ubuntu 20.04

And using Korge 2.0.7.1 (Apache 2.0 License) Korge Project template

Repo found at: https://github.com/korlibs/korge

Example usage, after cloning / downloading:

```./gradlew :runJvm```

Hope this may help you in your development work. - IanP

v0.2 - refactoring leaves and nodes, nodeline with noise, k-means clustering

meshline between five points with 60% noise :

![meshline](https://user-images.githubusercontent.com/13192685/112913775-a9602f00-90b7-11eb-900b-950758d1ce18.png)

clustering nodes into 10 rooms :

![kmeans_cluster](https://user-images.githubusercontent.com/13192685/112913824-c72d9400-90b7-11eb-99ed-77d15e4a3f16.png)

v0.1 - modelling organic cave-like environment rooms network :

![example output for renderOakLeafAngled()](nodes_oakLeaf.png "example output for renderOakLeafAngled()")

yellow nodes are from three 'oakleaf trees', blue nodes are connectivity and consolidation along the yellow node lines :

![example output for renderNodeMeshStationary()](nodes_mesh_consolidated.png "example output for renderNodeMeshStationary()")

