1. When you run your app and rotate the device/emulator are the method counts counts consistent
with the number of times you see the methods called in the log?

With the current code in the source file, method counts are consistent with the number of times methods calls are logged in the log.

Observation: I am restoring the saved count values in onCreate(). When the values are printed on the UI in the respective events, 
onCreate and onStart counts seemed not incrementing on the UI, though the log clearly showed the count variables are incremented.
This happened because onRestorentInstanceState called by the parent class restores the values in the UI widgets. 
onRestorentInstanceState() is called after onStart and before onResume methods are called.
Although  onCreate and onStart count values are updated in the widget in  the respective events, it is getting overwritten 
when the parent class calls the onRestorentInstanceState(which would be the previous widget values stored in 
onSaveInstanceState by the parent class). When the onCreate and onStart count values are set inside onResume, 
UI is updated with correct values(as it comes after onRestoreInstanceState call by the parent class).
Generalized it by clubbing setText statements for all the events inside a method called updateCountOnUI() and called it in every life cycle event.
Counts are printed consistently with the number of time the methods are called.



2. Save the values of your field that count the times each method is called using onRestoreInstanceState
and onSaveInstanceState. See below for an example of saving and restoring a
field. How does this affect the displayed values when you rotate the device/emulator?
! protected void onRestoreInstanceState(Bundle savedInstanceState) {
! ! super.onRestoreInstanceState(savedInstanceState);
! ! if (savedInstanceState != null) {
! ! ! paused = savedInstanceState.getInt("paused");
! ! }
! }
! protected void onSaveInstanceState(Bundle outState) {
! ! super.onSaveInstanceState(outState);
! ! outState.putInt("paused", paused);
! }

Observation: onCreate and onStart counts are 1 when the application is lauched and remains same during rotation. Other counts are incremented properly.
Reason: Since onRestoreInstanceState is called after onCreate and onStart, the restored values are not available for the onCreate and onStart to increment.
It always increments the initialized value.

1. One solution is to restore the values inside onCreate and not override onRestoreInstanceState.
2. Another solution is to add the restored values to the count variables inside onRestoreInstanceState method.

@Override
protected void onRestoreInstanceState(Bundle savedInstanceState) {
	if (savedInstanceState != null) {
		createCount += savedInstanceState.getInt(KEY_CREATE, 0);
		restartCount += savedInstanceState.getInt(KEY_RESTART, 0);
		startCount += savedInstanceState.getInt(KEY_START, 0);
		resumeCount += savedInstanceState.getInt(KEY_RESUME, 0);
		pauseCount += savedInstanceState.getInt(KEY_PAUSE, 0);
	}	
}

3. Make count variables static and not override onSaveInstance or onRestoreInstanceState.

3. Change one or all of the EditViews to a TextView. After all the user does not need to edit
the values of the count. How does this affect the displayed values when you rotate the
device/emulator?

Observation: Changing EditView to TextView did not affect the displayed values. It just made the field read only and changed how the value appeared on the screen.
