# Floating Action View
A simple View anchored to the Floating Action Button

![Sample](https://github.com/br00/android-floating-action-view/blob/master/screenshots/floating_view_2.gif)![Sample](https://github.com/br00/android-floating-action-view/blob/master/screenshots/floating_view_3.gif)

## Usage

```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.design.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true">

    ...

    <com.alessandroborelli.design.FloatingActionView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:fab="@+id/fab"
        app:outsideColor="@color/outsideFAView"
        app:useFabMargin="true">
    </com.alessandroborelli.design.FloatingActionView>

</android.support.design.widget.CoordinatorLayout>
```
###Custom attributes

- `fab` Resource Id of the fab
- `outsideColor` Color of view behind the floating view 
- `useFabBackgroundColor` Assign to the floating view same fab background color
- `useFabMargin` Assign to the floating view same fab margin
- `useFabSize` Assign to the floating view same fab size (use "width" or "height")

## License

    Copyright 2017 Alessandro Borelli

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
