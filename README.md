# react native widget example
This is a description of how to implement widget in React Native.

> Init a new react-native project
```
$ react-native init ${NameOfYourChoice}
```

> Open up the project and modify index.ios.js / index.android.js to the following code
```
import { AppRegistry } from 'react-native';
import App from './js/App';

AppRegistry.registerComponent('recapp', () => App);

```

> Then, create a folder ”js” with a new file called “App.js” and insert the following code
```
import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View
} from 'react-native';

export default class App extends Component {
  render() {
    const { navigationKey, medication } = this.props;
    console.log(this.props);
    if (navigationKey === 'MedicationScreen') {
      return (
        <View style={styles.container}>
          <Text style={styles.instructions}>
            I am in medication screen !
          </Text>
          <Text style={styles.instructions}>
            {medication.medId} {medication.medName}
          </Text>
        </View>
      );
    }
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          Welcome to React Native!
        </Text>
        <Text style={styles.instructions}>
          To get started, edit index.android.js
        </Text>
        <Text style={styles.instructions}>
          Double tap R on your keyboard to reload,{'\n'}
          Shake or press menu button for dev menu
        </Text>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});

```
> So, we should be able to get the initialProps that passed in as  “navigationKey” (to determine which page to route to) and “medication” (the object to be display once open the medication screen)

> In order to implement widget in both ios & android that pass in data to RN, we have to write it natively since official RN does not support widget as of now.

---

## Implementation For Android
> Open the project in android studio (/android)
	- Right click on “app/src/res” and select New > Widget > App Widget
	- Give it a class name (eg. MyWidget.java) and the Layout file + Class will be generated
	- Open the MyWidget.java file and modify the onUpdate method
```
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, CustomReactActivity.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);  // Identifies the particular widget...
            intent.putExtra("module", "recapp");

            Bundle b = new Bundle();
            b.putString("navigationKey","MedicationScreen");

            Bundle med = new Bundle();
            med.putString("medId", "P0001");
            med.putString("medName", "Panadol");

            b.putBundle("medication",med);

            intent.putExtra("data",b);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Make the pending intent unique...
            PendingIntent pendIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.medication_widget);
            views.setOnClickPendingIntent(R.id.appwidget_text, pendIntent);
            appWidgetManager.updateAppWidget(appWidgetId,views);
        }

    }
```
> This step is basically used to instantiate an Intent and pass in necessary data and fire the updateAppWidget() method.

> Data to be passed can be modified to your preference :p

> The next step is to create a CustomReactActivity.java since the MainActivity doesn’t allow us to pass in initialProps
	- After created the new class, make it extends ReactActivity and implements DefaultHardwareBackBtnHandler (this is to handle the back button behavior when click on hardware back button in android)
	- Then, insert the following codes:

```
    private ReactRootView mReactRootView;
    private ReactInstanceManager mReactInstanceManager;
    private String extraModule;
    private Bundle extraBundle;

    private ReactInstanceManagerBuilder getBuilder(){
        return ReactInstanceManager.builder()
                .setApplication(getApplication())
//                .setBundleAssetName("index.android.bundle")
//                .setUseDeveloperSupport(false)
                .setBundleAssetName("index.android.bundle")
                .setUseDeveloperSupport(true)
                .setJSMainModuleName("index.android");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mReactRootView = new ReactRootView(this);
        mReactInstanceManager = getBuilder()
                .addPackage(new MainReactPackage())
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build();
        Bundle bundle = getIntent().getExtras();
        extraModule = bundle.getString("module", "");
        extraBundle = bundle.getBundle("data");

        startReactApplication();
    }

    private void startReactApplication() {
        mReactRootView.startReactApplication(mReactInstanceManager, extraModule, extraBundle);
        setContentView(mReactRootView);
    }
```
> This step is to override the existing startReactApplication() and pass in additional data as initialProps to React Native.

> Tada, android part is done ~

---

## Implementation For iOS
> Open the project in xcode (/ios)
	- In iOS, the widget is called “Today Extension”.
	- So, select the project in xcode left column,  then click the menu bar “Editor > Add Target > Today Extension” and give it a name (Eg. MyWidget) and click finish.
	- It will appear as a new folder and consist of swift file + storyboard + info.plist
	- Open up the storyboard of the Widget and remove the existing Text. Then add a Button into it (Because Text cannot fire action while Button can)
	- Then open both swift file and storyboard side by side. Point the mouse cursor to the Button you just created and Ctrl + Drag it to the swift file.
	- A new method shall be created in the swift file, change the method to the following
```
    @IBAction func openMedicationScreen(_ sender: Any) {
        let myAppUrl = URL(string: "recapp://P0001")!
        extensionContext?.open(myAppUrl, completionHandler: { (success) in
            if (!success) {
                print("error: failed to open app from Today Extension")
            }
        })
    }
```
> this step is to open the app using url whereas “recapp” is the url scheme that being defined in Info.plist

> In order to allow this to work, we need to allow our app to be opened using url scheme.

> Open the Info.plist for the project, not the 1 in MyWidget. Right click on it and Open As > Source Code. Then, add in the following item as first item in <dict>
```
<key>CFBundleURLTypes</key>
	<array>
		<dict>
			<key>CFBundleURLName</key>
			<string>org.reactjs.native.example.recapp</string>
			<key>CFBundleURLSchemes</key>
			<array>
				<string>recapp</string>
			</array>
		</dict>
	</array>
```
> the package name should be change to your package name and the “recapp” can be change to anything that you wan to be called from other app. (TodayExtension is actually widget living in another app, so that our widget cannot open our app directly. Instead, must open via URL scheme)

> Then, open AppDelegate.m and modify the existing code to following:
```

#import "AppDelegate.h"

#import <React/RCTBundleURLProvider.h>
#import <React/RCTRootView.h>

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
  NSLog(@"************* Data:%@", self.data);
  if (self.data == nil) {
  NSURL *jsCodeLocation;

  jsCodeLocation = [[RCTBundleURLProvider sharedSettings] jsBundleURLForBundleRoot:@"index.ios" fallbackResource:nil];

  RCTRootView *rootView = [[RCTRootView alloc] initWithBundleURL:jsCodeLocation
                                                      moduleName:@"recapp"
                                               initialProperties:nil
                                                   launchOptions:launchOptions];
  rootView.backgroundColor = [[UIColor alloc] initWithRed:1.0f green:1.0f blue:1.0f alpha:1];

  self.window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
  UIViewController *rootViewController = [UIViewController new];
  rootViewController.view = rootView;
  self.window.rootViewController = rootViewController;
  [self.window makeKeyAndVisible];
  return YES;
  }
  return NO;
}

- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url
  options:(NSDictionary<NSString *, id> *)options
{
  NSLog(@"URL scheme: %@", url);
  NSString *urlString = url.absoluteString;
  self.data = [urlString componentsSeparatedByString:@"://"][1];

  NSURL *jsCodeLocation;

  jsCodeLocation = [[RCTBundleURLProvider sharedSettings] jsBundleURLForBundleRoot:@"index.ios" fallbackResource:nil];
  NSDictionary *medication = @{@"medId" : self.data, @"medName":@"Panadol from iOS"};
  NSDictionary *props = @{@"medication":medication, @"navigationKey":@"MedicationScreen"};
  RCTRootView *rootView = [[RCTRootView alloc] initWithBundleURL:jsCodeLocation
                                                      moduleName:@"recapp"
                                               initialProperties:props
                                                   launchOptions:options];
  rootView.backgroundColor = [[UIColor alloc] initWithRed:1.0f green:1.0f blue:1.0f alpha:1];

  self.window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
  UIViewController *rootViewController = [UIViewController new];
  rootViewController.view = rootView;
  self.window.rootViewController = rootViewController;
  [self.window makeKeyAndVisible];
  return YES;
}

@end

```

> The first method didFinishLaunchingWithOptions is an existing method, but we need to tune it so that the second method will be called when we click on the widget button.

> So, we check for the data being passed in. If data is is null (means there’s no data being pass in when we open the app), we will proceed with existing code to startup RN module.

> Else, when there is data, we should return NO. So that the second method openURL will be fired accordingly.

> The second method will be checking for the url being called from Today app, and separate it into our data. Then, pass that data to RN module.

> Tada, that’s all for the description. :p
