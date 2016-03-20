#Note

__MainActivity.java (AndroidLauncher.java)__ can be found [here](https://github.com/KOgroup/AndroidApp/blob/master/src/com/kogroup/anglemeter/AndroidLauncher.java)

__AngleMeter.java__ can be found [here](https://github.com/KOgroup/AndroidApp/blob/master/core/src/com/kogroup/anglemeter/AngleMeter.java)

__SavedData.java__ can be found [here](https://github.com/KOgroup/AndroidApp/blob/master/core/src/com/kogroup/anglemeter/SavedData.java)

__Assets__ are packaged in atlas.png

__Angle Meter All-In-One__

Angle meter is an app that allows you to measure angles by tilting your phone accordingly. It's a very handy app, especially when constructing something (let's say you want to hammer two wooden boards at the 45 degree angle) or when you need to know if things are horizontal or vertical (similar to water level). This app has a friendly UI. At the begging the user has to calibrate their phone (meaning that he/she puts a phone vertically and clicks calibrate) so the angles are as precise as possible. Then a user can move the phone freely and the current angle will be displayed. User can also choose a measuring tape - this has nothing to do with angles directly - by dragging the tape (on the screen) the current length is displayed. Usually, the maximum length is limited with the phone's size (although we are thinking about implementing the idea of dragging the tape as long as you like and the user can just move the phone accordingly to the opposite direction so he/she isn't limited by the phone's size). The calculation of angles is based on accelerometers in smartphones. In this case, the Y-axis accelerometer is taken - it has the highest value when phone is in portrait position and zero when in landscape. Then a function transforms given values into a suitable result. The measuring tape needs the phone's pixel to centimeter/inch constant and then returns the length.
