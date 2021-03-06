package com.example.android.arkanoid;

public interface Levels {
     int Livello1MARIO[][]= new int[][]{
             {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
             {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
             {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
             {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
             {0, 0, 6, 6, 6, 6, 6, 0, 0, 0}, //PRIMA RIGA 16x9
             {0, 6, 6, 6, 6, 6, 6, 6, 6, 0},
             {0, 6, 14, 14, 13, 8, 13, 0, 0, 0},
             {0, 13, 14, 13, 13, 8, 13, 13, 0, 0},
             {0, 13, 14, 13, 13, 13, 8, 13, 0, 0},
             {0, 14, 14, 13, 13, 8, 8, 8, 8, 0},
             {0, 0, 13, 13, 13, 13, 13, 13, 0, 0},
             {0, 6, 6, 1, 6, 6, 6, 1, 6, 6},
             {0, 6, 6, 1, 6, 6, 6, 1, 6, 6},
             {0, 6, 6, 7, 1, 1, 1, 7, 6, 6},
             {0, 13, 1, 1, 1, 1, 1, 1, 1, 13},
             {0, 13, 1, 1, 1, 1, 1, 1, 1, 13},
             {0, 0, 1, 1, 1, 1, 1, 1, 1, 0},
             {0, 0, 1, 1, 0, 0, 0, 1, 1, 0},
             {0, 0, 1, 1, 0, 0, 0, 1, 1, 0},
             {0, 0, 8, 8, 0, 0, 0, 8, 8, 0}};

    int Livello2PIKACHU[][]= new int[][]{
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 8, 0, 0, 0, 0, 0, 0, 0, 8}, //PRIMA RIGA 16x9, salta la prima colonna
            {0, 7, 8, 0, 0, 0, 0, 0, 8, 7},
            {0, 7, 7, 8, 0, 0, 0, 8, 7, 7},
            {0, 7, 7, 7, 8, 8, 8, 7, 7, 7},
            {0, 8, 7, 7, 7, 7, 7, 7, 7, 8},
            {0, 8, 7, 7, 7, 7, 7, 7, 7, 8},
            {0, 7, 7, 8, 7, 7, 8, 7, 7, 7},
            {0, 7, 7, 11, 8, 7, 11, 8, 7, 7},
            {0, 7, 7, 8, 7, 7, 8, 7, 7, 7},
            {0, 7, 7, 7, 7, 7, 7, 7, 7, 7},
            {0, 7, 7, 6, 7, 8, 7, 6, 7, 7},
            {0, 7, 7, 6, 7, 7, 7, 6, 7, 7},
            {0, 8, 7, 7, 7, 8, 7, 7, 7, 8},
            {0, 0, 8, 7, 8, 13, 8, 7, 8, 0},
            {0, 0, 0, 8, 8, 13, 8, 8, 0, 0},
            {0, 0, 0, 0, 8, 8, 8, 0, 0, 0}};

    int Livello3ZELDA[][]= new int[][]{
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 8, 8, 8, 0, 0, 0}, //PRIMA RIGA 16x9, salta la prima colonna
            {0, 0, 0, 8, 2, 2, 2, 8, 0, 0},
            {0, 0, 8, 2, 2, 2, 2, 2, 8, 0},
            {0, 8, 2, 2, 2, 7, 7, 7, 7, 8},
            {0, 2, 2, 2, 7, 7, 7, 7, 7, 8},
            {0, 2, 2, 7, 7, 7, 7, 7, 7, 7},
            {0, 2, 8, 7, 7, 8, 8, 8, 7, 7},
            {0, 8, 7, 7, 8, 13, 13, 13, 8, 7},
            {0, 7, 7, 8, 13, 13, 13, 13, 13, 8},
            {0, 7, 8, 13, 8, 13, 13, 8, 13, 7},
            {0, 8, 7, 8, 15, 13, 13, 15, 8, 7},
            {0, 0, 7, 13, 8, 13, 13, 8, 13, 7},
            {0, 0, 8, 13, 13, 13, 13, 13, 13, 8},
            {0, 0, 8, 13, 13, 4, 4, 13, 13, 8},
            {0, 0, 0, 8, 13, 13, 13, 13, 8, 0},
            {0, 0, 0, 0, 8, 13, 13, 8, 0, 0}};

    int Livello4IRONMAN[][]= new int[][]{
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 8, 6, 6, 6, 8, 0, 0}, //PRIMA RIGA 16x9, salta la prima colonna
            {0, 0, 8, 6, 6, 6, 6, 6, 8, 0},
            {0, 8, 6, 6, 6, 6, 6, 6, 6, 8},
            {0, 8, 6, 7, 7, 6, 7, 7, 6, 8},
            {0, 6, 7, 7, 7, 7, 7, 7, 7, 6},
            {0, 6, 7, 7, 7, 7, 7, 7, 7, 6},
            {0, 6, 7, 15, 15, 7, 15, 15, 7, 6},
            {0, 6, 7, 7, 7, 7, 7, 7, 7, 6},
            {0, 6, 7, 7, 7, 7, 7, 7, 7, 6},
            {0, 6, 6, 7, 7, 7, 7, 7, 6, 6},
            {0, 6, 6, 7, 7, 7, 7, 7, 6, 6},
            {0, 8, 6, 7, 8, 8, 8, 7, 6, 8},
            {0, 8, 6, 7, 7, 7, 7, 7, 6, 8},
            {0, 0, 8, 6, 7, 7, 7, 6, 8, 0},
            {0, 0, 0, 8, 6, 6, 6, 8, 0, 0},
            {0, 0, 0, 0, 8, 6, 8, 0, 0, 0}};

    int Livello5FANTASMINO[][]= new int[][]{
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 4, 4, 4, 0, 0, 0}, //PRIMA RIGA 16x9, salta la prima colonna
            {0, 0, 0, 4, 4, 4, 4, 4, 0, 0},
            {0, 0, 4, 4, 4, 4, 4, 4, 4, 0},
            {0, 0, 4, 4, 4, 4, 4, 4, 4, 0},
            {0, 4, 4, 4, 4, 4, 4, 4, 4, 4},
            {0, 4, 4, 11, 11, 4, 11, 11, 4, 4},
            {0, 4, 4, 11, 12, 4, 11, 12, 4, 4},
            {0, 4, 4, 11, 12, 4, 11, 12, 4, 4},
            {0, 4, 4, 4, 4, 4, 4, 4, 4, 4},
            {0, 4, 4, 4, 4, 4, 4, 4, 4, 4},
            {0, 4, 4, 11, 4, 11, 4, 11, 4, 4},
            {0, 4, 11, 4, 11, 4, 11, 4, 11, 4},
            {0, 4, 4, 4, 4, 4, 4, 4, 4, 4},
            {0, 4, 4, 4, 4, 4, 4, 4, 4, 4},
            {0, 4, 4, 0, 4, 4, 4, 0, 4, 4},
            {0, 4, 0, 0, 0, 4, 0, 0, 0, 4}};

    int Livello6PACMAN[][]= new int[][]{
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 8, 8, 8, 8, 0, 0, 0}, //PRIMA RIGA 16x9, salta la prima colonna
            {0, 0, 8, 7, 7, 7, 7, 8, 0, 0},
            {0, 8, 7, 7, 7, 7, 7, 7, 8, 0},
            {0, 7, 7, 7, 7, 8, 7, 7, 7, 8},
            {0, 7, 7, 7, 7, 8, 7, 7, 7, 8},
            {0, 7, 7, 7, 7, 7, 7, 7, 8, 0},
            {0, 7, 7, 7, 7, 7, 7, 8, 0, 0},
            {0, 7, 7, 7, 7, 7, 8, 0, 0, 11},
            {0, 7, 7, 7, 7, 8, 0, 0, 0, 11},
            {0, 7, 7, 7, 7, 7, 8, 0, 0, 0},
            {0, 7, 7, 7, 7, 7, 7, 8, 0, 0},
            {0, 7, 7, 7, 7, 7, 7, 7, 8, 0},
            {0, 7, 7, 7, 7, 7, 7, 7, 7, 8},
            {0, 8, 7, 7, 7, 7, 7, 7, 8, 0},
            {0, 0, 8, 7, 7, 7, 7, 8, 0, 0},
            {0, 0, 0, 8, 8, 8, 8, 0, 0, 0}};

    int Livello7BATMAN[][]= new int[][]{
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 8, 8, 0, 0, 0, 0, 0, 8, 8}, //PRIMA RIGA 16x9, salta la prima colonna
            {0, 8, 16, 8, 0, 0, 0, 8, 16, 8},
            {0, 8, 16, 8, 8, 8, 8, 8, 16, 8},
            {0, 8, 16, 16, 8, 8, 8, 16, 16, 8},
            {0, 8, 16, 16, 16, 16, 16, 16, 16, 8},
            {0, 8, 16, 11, 16, 16, 16, 11, 16, 8},
            {0, 8, 16, 11, 11, 16, 11, 11, 16, 8},
            {0, 8, 16, 11, 11, 16, 11, 11, 16, 8},
            {0, 8, 16, 16, 16, 16, 16, 16, 16, 8},
            {0, 8, 16, 16, 16, 16, 16, 16, 16, 8},
            {0, 8, 16, 16, 17, 17, 17, 16, 16, 8},
            {0, 8, 16, 17, 13, 13, 13, 17, 16, 8},
            {0, 8, 16, 17, 13, 13, 13, 17, 16, 8},
            {0, 8, 16, 17, 13, 13, 13, 17, 16, 8},
            {0, 8, 16, 17, 13, 13, 13, 17, 16, 8},
            {0, 0, 8, 17, 8, 8, 8, 17, 8, 0}};

    int Livello8SFERA[][]= new int[][]{
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //PRIMA RIGA 16x9, salta la prima colonna
            {0, 0, 0, 0, 8, 8, 8, 0, 0, 0},
            {0, 0, 0, 8, 7, 7, 7, 8, 0, 0},
            {0, 0, 8, 7, 7, 7, 7, 7, 8, 0},
            {0, 0, 8, 7, 7, 7, 7, 7, 8, 0},
            {0, 8, 7, 7, 3, 3, 3, 7, 7, 8},
            {0, 8, 7, 7, 7, 7, 3, 7, 7, 8},
            {0, 8, 7, 7, 7, 3, 7, 7, 7, 8},
            {0, 8, 7, 7, 3, 7, 7, 7, 7, 8},
            {0, 8, 7, 7, 3, 3, 3, 7, 7, 8},
            {0, 8, 7, 7, 7, 7, 7, 7, 7, 8},
            {0, 0, 8, 7, 7, 7, 7, 7, 8, 0},
            {0, 0, 8, 7, 7, 7, 7, 7, 8, 0},
            {0, 0, 0, 8, 7, 7, 7, 8, 0, 0},
            {0, 0, 0, 0, 8, 8, 8, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};

    int Livello9FIORE[][]= new int[][]{
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 3, 3, 3, 3, 3, 0, 0}, //PRIMA RIGA 16x9, salta la prima colonna
            {0, 0, 3, 3, 7, 7, 7, 3, 3, 0},
            {0, 3, 3, 7, 7, 7, 7, 7, 3, 3},
            {0, 3, 7, 7, 16, 7, 16, 7, 7, 3},
            {0, 3, 7, 11, 16, 7, 16, 11, 7, 3},
            {0, 3, 7, 11, 16, 11, 16, 11, 7, 3},
            {0, 3, 7, 7, 7, 7, 7, 7, 7, 3},
            {0, 3, 3, 7, 7, 7, 7, 7, 3, 3},
            {0, 3, 3, 3, 7, 7, 7, 3, 3, 3},
            {0, 0, 0, 3, 3, 3, 3, 3, 0, 0},
            {0, 0, 0, 0, 3, 10, 3, 0, 0, 0},
            {0, 10, 10, 0, 0, 10, 0, 0, 10, 10},
            {0, 10, 10, 10, 0, 10, 0, 10, 10, 10},
            {0, 0, 10, 10, 0, 10, 0, 10, 10, 0},
            {0, 0, 0, 10, 10, 10, 10, 10, 0, 0},
            {0, 0, 0, 0, 10, 10, 10, 0, 0, 0}};

    int Livello10CREEPER[][]= new int[][]{
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 2, 2, 2, 2, 2, 2, 2, 2, 2}, //PRIMA RIGA 16x9, salta la prima colonna
            {0, 2, 2, 2, 2, 2, 2, 2, 2, 2},
            {0, 2, 2, 2, 2, 2, 2, 2, 2, 2},
            {0, 2, 8, 8, 2, 2, 2, 8, 8, 2},
            {0, 2, 8, 8, 2, 2, 2, 8, 8, 2},
            {0, 2, 8, 8, 2, 2, 2, 8, 8, 2},
            {0, 2, 2, 2, 2, 2, 2, 2, 2, 2},
            {0, 2, 2, 2, 2, 8, 2, 2, 2, 2},
            {0, 2, 2, 2, 8, 8, 8, 2, 2, 2},
            {0, 2, 2, 2, 8, 8, 8, 2, 2, 2},
            {0, 2, 2, 2, 8, 8, 8, 2, 2, 2},
            {0, 2, 2, 2, 8, 8, 8, 2, 2, 2},
            {0, 2, 2, 2, 8, 2, 8, 2, 2, 2},
            {0, 2, 2, 2, 2, 2, 2, 2, 2, 2},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};

    int Livello11PIG[][]= new int[][]{
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 4, 4, 4, 4, 4, 4, 4, 4, 4}, //PRIMA RIGA 16x9, salta la prima colonna
            {0, 4, 4, 4, 4, 4, 4, 4, 4, 4},
            {0, 4, 4, 4, 4, 4, 4, 4, 4, 4},
            {0, 4, 4, 4, 4, 4, 4, 4, 4, 4},
            {0, 4, 8, 11, 4, 4, 4, 11, 8, 4},
            {0, 4, 8, 11, 4, 4, 4, 11, 8, 4},
            {0, 4, 4, 4, 4, 4, 4, 4, 4, 4},
            {0, 4, 4, 4, 4, 4, 4, 4, 4, 4},
            {0, 4, 4, 4, 13, 13, 13, 4, 4, 4},
            {0, 4, 4, 4, 9, 13, 9, 4, 4, 4},
            {0, 4, 4, 4, 9, 13, 9, 4, 4, 4},
            {0, 4, 4, 4, 13, 13, 13, 4, 4, 4},
            {0, 4, 4, 4, 4, 4, 4, 4, 4, 4},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 4},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};

    int LivelloMOSTRO[][]= new int[][]{
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 12, 0, 0, 0, 0, 0, 0, 0, 12}, //PRIMA RIGA 16x9, salta la prima colonna
            {0, 12, 12, 0, 0, 0, 0, 0, 12, 12},
            {0, 0, 12, 12, 0, 0, 0, 12, 12, 0},
            {0, 0, 0, 12, 12, 12, 12, 12, 0, 0},
            {0, 0, 0, 12, 12, 12, 12, 12, 0, 0},
            {0, 0, 12, 12, 12, 12, 12, 12, 12, 0},
            {0, 0, 12, 12, 6, 12, 6, 12, 12, 0},
            {0, 0, 12, 12, 12, 12, 12, 12, 12, 0},
            {0, 0, 12, 12, 12, 12, 12, 12, 12, 0},
            {0, 12, 12, 12, 12, 12, 12, 12, 12, 12},
            {0, 12, 12, 12, 0, 0, 0, 12, 12, 12},
            {0, 12, 0, 12, 12, 0, 12, 12, 0, 12},
            {0, 12, 0, 12, 0, 0, 0, 12, 0, 12},
            {0, 12, 0, 12, 12, 0, 12, 12, 0, 12},
            {0, 0, 0, 0, 12, 0, 12, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};

    int Livello1LANDSCAPE[][]= new int[][]{
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            {0, 0, 2, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            {0, 0, 2, 0, 2, 3, 3, 3, 4, 0, 0, 5, 0, 0, 6, 6, 6 },
            {0, 0, 2, 2, 2, 3, 0, 0, 4, 0, 0, 5, 0, 0, 6, 0, 6 },
            {0, 0, 2, 0, 2, 3, 3, 3, 4, 0, 0, 5, 0, 0, 6, 0, 6 },
            {0, 0, 2, 0, 2, 3, 0, 0, 4, 0, 0, 5, 0, 0, 6, 0, 6 },
            {0, 0, 2, 0, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6 }};

    int Livello2LANDSCAPE[][]= new int[][]{
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },      //PRIMA RIGA 6x15, salta la prima colonna
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            {0, 0, 15, 15, 15, 2, 15, 2, 2, 2, 2, 2, 15, 2, 15, 15, 15 },
            {0, 0, 15, 15, 15, 15, 2, 2, 2, 2, 2, 2, 2, 15, 15, 15, 15 },
            {0, 0, 15, 15, 15, 15, 2, 15, 2, 2, 2, 15, 2, 15, 15, 15, 15 },
            {0, 0, 15, 15, 15, 2, 2, 2, 2, 2, 2, 2, 2, 2, 15, 15, 15 },
            {0, 0, 15, 15, 15, 2, 2, 2, 2, 2, 2, 2, 2, 2, 15, 15, 15 },
            {0, 0, 15, 15, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 15, 15 }};

    int Livello3LANDSCAPE[][]= new int[][]{
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            {0, 0, 2, 2, 2, 2, 2, 11, 11, 11, 11, 11, 6, 6, 6, 6, 6 },
            {0, 0, 2, 2, 2, 2, 2, 11, 11, 11, 11, 11, 6, 6, 6, 6, 6 },
            {0, 0, 2, 2, 2, 2, 2, 11, 11, 11, 11, 11, 6, 6, 6, 6, 6 },
            {0, 0, 2, 2, 2, 2, 2, 11, 11, 11, 11, 11, 6, 6, 6, 6, 6 },
            {0, 0, 2, 2, 2, 2, 2, 11, 11, 11, 11, 11, 6, 6, 6, 6, 6 },
            {0, 0, 2, 2, 2, 2, 2, 11, 11, 11, 11, 11, 6, 6, 6, 6, 6 }};
}
