package main.java;

import static spark.Spark.*;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.json.*;

public class Main {
	
    public static void main(String[] args) {
    	
    	//basic help response to a blank call to the webpage
    	get("/", (request, response) -> 
    	{
    		return "Invalid Request\nTo search for a movie use: /search/movie/:movieName\nTo request a movie use:/request/movie/:movieId";
		});
    }
}