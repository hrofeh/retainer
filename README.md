Retainer
============
An android library which enables activity/fragment fields retention through configuration changes (e.g screen rotatio) by generating boilerplate code for you.

 * Retain any object and maintain reference through configuration changes (no need for slow serialization-deserialization process).
 * Makes MVVM implemntation cleaner and shorter by retaining the ViewModel.
 * Simple and straight forward use.

```java
class MainActivity extends AppCompatActivity{

    @Retain
    ViewModel mViewModel;

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
}

```

Download
--------

```groovy
dependencies {
  compile 'com.hrh:retainer:[latest-version]'
  annotationProcessor 'com.hrh:retainer-compiler:[latest-version]'
}
```

How does it work
--------
Retainer creates and handles an headless retained fragment which wraps a mapping of all your @Retain fields, as simple as that.

In order to keep runtime impact minimal, Retainer uses mostly code generation while reflection is only used for constructing and caching a retainer object for your activity/fragment when it is first created.

Watch out for memory (especially context) leaks
--------
Marking an non-primitive field as @Retained means it won't be released when a configuration change occures.
Watch out for memory leaks and never retain an activity/fragment or an object holding a refernce to one.

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