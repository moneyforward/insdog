# Tips

In this section, we discuss a few tips and tricks that might be useful.

## Accessing Model  as an Interface
Also, consider defining a set of reusable `Scene` returning methods as an `interface`.
They can be implemented by your **TestBase** class.
This approach will make sense if your application has a structure, where a larger component consists of combinations of smaller ones, and smaller ones are reused by larger ones.


## Codegen

To write a test using the **InsDog**, [`codegen`](https://playwright.dev/java/docs/codegen) is a very powerful and useful friend.
Please check it out and familiarize yourself with its way of playing DOM elements in HTMLs.


## Accessing model, assertion, and test class.