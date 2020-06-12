# WeatherWidget_JavaFX
I started this project to learn about how to use an API with java.

In this personal project, I made a weather widget using Open Weather API and JavaFX.

![1](https://github.com/RamizFaragalla/WeatherWidget_JavaFX/blob/master/screenshot1.PNG)

Main Features:
  - search bar to get the weather of any location in the world
  - Today tab displays current weather info as well as hourly forcast for that day
  - Tomorrow tab displays tomorrow's hourly forcast and the high and low temperatures of that day
  - 7 Days tab displays the high and low temperatures of the next 7 days
  - the widget automatically updates the weather every minute using AnimationTimer
  - the user can also manually click on the the refresh button

The main classes that were used in this project were API(parent), CurrentWeatherAPI(child), WeatherAPI(child).
  - API class made the HTTP request and returned a JSON object
  - CurrentWeatherAPI extracted the current weather data from the JSON object
  - WeatherAPI extracted the daily and hourly forcast
  
![1](https://github.com/RamizFaragalla/WeatherWidget_JavaFX/blob/master/screenshot2.PNG)
![1](https://github.com/RamizFaragalla/WeatherWidget_JavaFX/blob/master/screenshot3.PNG)
![1](https://github.com/RamizFaragalla/WeatherWidget_JavaFX/blob/master/screenshot4.PNG)
