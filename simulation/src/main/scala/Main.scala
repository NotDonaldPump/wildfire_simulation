import model.* 
@main def runSimulation(): Unit = {
    // starting a python web app from here
    // waiting for the user giving us at least the size and the duration in ticks and eventually the conditions
    // deserialize informations coming from the frontend
    // create a grid with the given size and conditions
    // iterating from 0 to the duration by calling the update method of the grid
    // every iteration we call the gridToJson method to convert the grid to a json object
    // and we send it to the frontend
    // at the end we ask the user if he wants to do a new simulation


    // add concurency when we have a working version
    // should I compute the whole simulation before sending it to the frontend or should I send it every tick ?
}