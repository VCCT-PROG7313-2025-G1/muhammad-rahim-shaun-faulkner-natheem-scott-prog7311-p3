# POE PART 3

# Corrections Made to POE Part 2 

Please note the issues that were corrected from the feedback given back to us from PART 2 was all done before part 3 features were implemented and the feedback we received were as follows:

•	Users should require to optionally take an in-app photo

•	Users should be able to view the image receipt attached to transactions.

•	The main layout pushes up into the status bar of the emulator. Layout discrepancies in a few components.

________________________________________
# Two Innovative features that were added

•	**Feature 1: (User bank accounts)** - Added functionality for user to create bank accounts with account type and that also stores balances to be used for the app. This is tied to the user and any transaction made by the user will be attached to the account they choose, in turn adding to the accounts balance.

•	**Feature 2: (Export to CSV) -** Added functionality to export analytical data to a CSV file per month which can be used by the user to add to an excel sheet.

________________________________________
# Android App Project

We created a mobile Android application that was made to track and manage the users spending across different categories. The app also includes the improved UI elements based on our lecturers feedback and now we have implemented features such as visual spending analysis with the use of graphs. Users are now able to view category expenses over a selected period of time. We also included how their spending aligns with a defined minimum and maximum goals. The platform also connects to an online database using Firebase for data storage and it retrieves all the records in real time in order to support a smooth and responsive user experience.
________________________________________

# WHAT CHANGED:
•	Data is stored on firebase to make use of online database, no more using RoomDB to store data.

•	Can now upload photos to firebase storage and image previews work as well as taking a photo to be stored.

•	Changed how min and max goals are stored to a firebase collection and changed the editable view to text views to display the goals on homepage.

•	Added bank account creation which now displays on homepage with balances and collective total balances of all accounts.

•	Added email field for users sign up for firebase authentication.

•	Added user profile view to see bank accounts, latest transactions with buttons to add account and take me to categories screen.

•	Added account view area of user bank accounts to edit accounts or delete bank accounts.

•	Added analytics pie chart and bar chart to that uses category, goal, income and expense data.

•	Added export to csv functionality to export analytical data to a csv in the phone downloads directory.

•	Huge UI overhaul on look and feel for majority of views.

•	Changed app logo icon and app name.

•	Made reusable burger menu for navigation and removed bottom nav bar.

•	Removed settings view.

•	Added account selection to transaction adding view.

________________________________________

# Android SDK Directory

Make sure your that your local local.properties file includes the correct Android SDK path: "sdk.dir=C:\Users\user\AppData\Local\Android\Sdk" user = should be your computer username

________________________________________
# GitHub Repository
Access the full source code of the entire project below:
👉 [https://github.com/hdimp/muhammad-rahim-shaun-faulkner-natheem-scott-prog7311-p3]
________________________________________
# YouTube Demo
Watch the demo showcasing that the android application runs on a mobile device and shows all the features that are required for POE Part 3 here:
🎬 [https://www.youtube.com/watch?v=fwoqncPoy7Y]
________________________________________
# WinRAR or Windows Zip
1.	Please use WinRAR or Windows Zip
2.	Please use this link if you need to download WinRAR: https://www.win-rar.com/predownload.html?&L=0.
3.	Then follow the prompts to install it.
4.	After that, you can then download the zip file from the GitHub repository, make sure use have WinRAR installed or you can use Windows Zip as it is working again.
5.	You must right click on the folder and choose "Extract All" this can be done for WinRAR or WindowsZip then save the file to your desired location.

![image](https://github.com/user-attachments/assets/bb972be5-ca36-444b-8e1d-bc653ebc6ff7)


________________________________________

# How to Import from GitHub
1.	Clone the repository using:
2.	Open Android Studio.
3.	Select File > Open** and navigate to the cloned project directory.
4.	Sync Gradle and let dependencies load.
5.	Build the project using Build > Rebuild Project.
________________________________________
# How to Start the Application
1.	Make sure you have an Android emulator, or a physical device connected.
2.	In Android Studio, click Run > Run 'app' or press Shift + F10.
3.	The app should be installed and launched on your selected device.
________________________________________

# Database Schema:
Firebase Firestore Database is used to store collections of users with their respective subcollections for better user management.

users/{userId}/
- email: “”
- fullName: “”
- userName: “”
	- /accounts/{accountId}/
 		- accountType: “”
 		- balance: 0
 		- bankName: “”
	- /categories/{categoryId}/
 		- name: “”
 		- transactionType: “”
	-/goals/{goalId}/
 		- maxGoal: 0
 		- minGoal: 0
 		- month: “”
 		- timeStamp: Timestamp.Now()
	-/transactions/{transactionId}/
 		- accountId: reference to accounts collection
		- amount: 0
		- category: “”
		- endTimestamp: “”
		- frequency: “”
		- imageUrl: “”
		- notes: “”
		- recurring: false
		- startTimestamp: “”
		- timestamp: “”
		- transactionType: “”
    
**File Storage:**

Make use of Firebase Storage to store files

**Data Images**

Images of the data getting successfully sent to the to FireBase:

![image](https://github.com/user-attachments/assets/5598431b-9b3a-40b5-8400-707503dcf4a2)

![image](https://github.com/user-attachments/assets/e3210267-9fe3-4395-a943-bdeb94b5aab3)

![image](https://github.com/user-attachments/assets/0e859e39-8696-4e8b-a1ec-c1e73c0373d5)

![image](https://github.com/user-attachments/assets/1bb2f2cb-4e0e-4075-ab01-5764d35a674a)

![image](https://github.com/user-attachments/assets/ee4baecc-e000-4ff1-bd5d-3ba557dff951)

![image](https://github.com/user-attachments/assets/6c7f1546-cae8-435e-b901-e8faa431021f)

________________________________________

# App Screenshots

**Landing Page View**

![image](https://github.com/user-attachments/assets/6cc45071-5fa9-40ff-8caa-74c909e8307d)

**Home View**

![image](https://github.com/user-attachments/assets/469d28eb-19eb-4264-96ef-24eb022124fc)


**Login View**

![image](https://github.com/user-attachments/assets/fbf0eccb-0e0f-43df-b7df-b03533b07325)

**Register View**

![image](https://github.com/user-attachments/assets/5ddc39de-c616-4e66-b048-f24c253f2d89)

**Navigation View Menu**

![image](https://github.com/user-attachments/assets/3452c900-514e-4d93-9e02-5b24c709f58a)

**My Profile View**

![image](https://github.com/user-attachments/assets/15f8c1bc-7a41-40f6-b0a8-e4577b3f3c34)

**Add Transaction View**
 
![image](https://github.com/user-attachments/assets/abdaad4a-d344-42e6-bc00-797c3e754576)

**Upload View**

![image](https://github.com/user-attachments/assets/991f9d67-75cd-4a04-962c-da356ac0ea5a)

**Create Category Views**

![image](https://github.com/user-attachments/assets/05be1ed1-035d-43d2-b880-7d008cae56bf)

![image](https://github.com/user-attachments/assets/18a00a8d-9e69-4012-8f33-f15aa5dd04a1)

![image](https://github.com/user-attachments/assets/60d8e30a-6246-4e2b-8a9a-79d3c7b34e6d)

**Timeline and Analytics View**

![image](https://github.com/user-attachments/assets/2fc57b51-98a6-4ce1-bb9d-3af5213b1098)

![image](https://github.com/user-attachments/assets/4bd00119-3f84-442b-aa43-6012436f1966)

**Transaction Detail View**

![image](https://github.com/user-attachments/assets/4d8e3872-8a60-4ec7-b180-bcb9bc16ee0a)

**Set Monthly Goals**

![image](https://github.com/user-attachments/assets/14febe89-8209-4a0d-9ee6-1f0bb44a9443)

**Edit Categories**

![image](https://github.com/user-attachments/assets/b3bdf422-999f-45c6-b0e3-6a6c3e590986)

________________________________________
# References

- Dora, A. (2018). *RecyclerView Item Click Listener the Right Way*. Stack Overflow. [Link](https://stackoverflow.com/questions/49969278/recyclerview-item-click-listener-the-right-way)  
- mike-gallego (2018). *Struggling with context inflating a RecyclerView list item*. Stack Overflow. [Link](https://stackoverflow.com/questions/52224165/struggling-with-context-inflating-a-recyclerview-list-item)  
- Ong, A. (2022). *Why my recyclerView show Unresolved reference: recyclerView*. Stack Overflow. [Link](https://stackoverflow.com/questions/71604788/why-my-recyclerview-show-unresolved-reference-recyclerview)  
- reibuehl (2018). *How to set Calendar object to current date but time from SimpleDateFormat that contains HH:mm:ss*. Stack Overflow. [Link](https://stackoverflow.com/questions/48647950/how-to-set-calendar-object-to-current-date-but-time-from-simpledateformat-that-c)
- reibuehl (2018). *How to set Calendar object to current date but time from SimpleDateFormat that contains HH:mm:ss*. Stack Overflow. [Link](https://stackoverflow.com/questions/48647950/how-to-set-calendar-object-to-current-date-but-time-from-simpledateformat-that-c)
- ChatGpt (2025). *How to fix the nav bar and assist with bug fixes*. [Link](https://chatgpt.com)
- Kotlin Help. (2025). writeText. [Link](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.io/write-text.html )
- Maima, M. (2022). (Kotlin) Implementing a Barchart and Piechart using MPAndroidChart. Medium. [Link](https://malcolmmaima.medium.com/kotlin-implementing-a-barchart-and-piechart-using-mpandroidchart-8c7643c4ba75)

________________________________________
# Developed With

•	Kotlin

•	XML

•	Android Studio

•	RecyclerView, LiveData, ViewModel, FireBase
________________________________________
# Author

•	Natheem Scott ST10109685

•	Shaun Faulkner ST10034664

•	Muhammad Rahim ST10043611
