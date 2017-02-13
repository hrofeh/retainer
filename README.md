Retainer
============
Activity/fragment fields retention through configuration changes library which uses annotation processing to generate boilerplate
code for you.

 * Retain any object and maintain reference through configuration changes (no need for slow serialization-deserialization process).
 * Simple and straight forward use.

```java
class MainActivity extends AppCompatActivity{

    @Retain
    User mUser;

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
