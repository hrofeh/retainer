Retainer
============
![Logo](icon/logo.png)

An android library which enables activity/fragment fields retention through configuration changes (e.g screen rotatio) by generating boilerplate code for you.

 * Retain any object and maintain reference through configuration changes (no need for slow serialization-deserialization process).
 * Makes MVVM implemntation cleaner and shorter by retaining the ViewModel.
 * Simple and straight forward use.

```java
class MainActivity extends AppCompatActivity{

    @Retain
    ViewModel mViewModel; //This field will be retained through configuration changes

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Retainer.restore(this);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Retainer.retain(this);
    }
    
    //Calls to retain() and restore() can be made from a base activity class
}

```

Download
--------

```groovy
dependencies {
  compile 'com.hrh:retainer:0.0.56'
  annotationProcessor 'com.hrh:retainer-compiler:0.0.56'
}
```

How does it work
--------
Retainer creates and handles an headless retained fragment which wraps a mapping of all your @Retain fields, as simple as that.

In order to keep runtime impact minimal, Retainer uses mostly code generation while reflection is only used for constructing and caching a retainer object for your activity/fragment when it is first created.

Watch out for memory (especially Context) leaks
--------
Marking a non-primitive field as @Retained means it won't be released when a configuration change occures.

Although Retainer makes sure at compile time that non of your @Retain fields is or is a subclass of Context, you should
watch out for memory leaks and never retain an object holding a refernce to an android Context.

When not to use
--------
Retainer is useful only in cases where your activty/fragment is recreated due to a configuration change.

If you wish to retain fields for an activity/fragment which is recreated after being destroyed by the OS then you should use the plain old savedInstanceState.

I recommend taking a look at [IcePick](https://github.com/frankiesardo/icepick)

License
-------

    Copyright 2017 Hanan Rofe Haim

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
