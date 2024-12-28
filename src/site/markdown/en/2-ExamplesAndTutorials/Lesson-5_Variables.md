# Lesson 5: Variables

In order to make your **Scenes** and **Acts** interact with each others, you can  define and reference "variables", which **InsDog** provides.

An **Act** can process only one variable at a time, while **Scenes** can interact with multiple variables. 

An **Act** doesn't perform by itself, but it performs only under a **Scene**.
A **Scene** has its own namespace for variables, which is called "Variable Store".
When a **Scene A**  depends on another **Scene B**, variables exported by **Scene B** are visible to **Scene A**.