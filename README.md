**How we detected the activities:**

 - We calculated the magnitude of the x,y,z from the accelerometer each time we got an update from the sensor manager.
 - We calculated the FFT with window size of 32.
 - We stored 15 reads of FFT before analysing it and figure out what kind of activity we might find ( For each read we were just using the first element of x and y and calculate the magnitude out of them. We expect that most of the information is stored in the first element of the FFT array)
 - We update user activity after each 15 FFT reads where we look at: 1- The average  of differences between the 15 fft values. 2- Number of changes between reads.
 - From these two numbers (average_differences and number_of_changes) we could distinguish the following activities :
 - Resting mode: average_differences < 10 or number_of_changes is 0
 - Walking mode: average_differences is between 10 and 20 and number_of_changes > 10
 - Running mode: average_differences > 20 and number_of_changes > 10 

