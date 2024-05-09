# Bug Handling

** This is an example article. Handling policy of bugs is still being discussed in bravo team as of Apr/30/2024. **

What is a bug? A Wikipedia article says:

> A software bug is an error, flaw or fault in the design, development, or operation of computer software that causes it to produce an incorrect or unexpected result, or to behave in unintended ways
[Software bug](https://en.wikipedia.org/wiki/Software_bug)

Then the next question is:
What is incorrect, unexpected, unintended result? 
How can we decide it?

It's why we should somehow define what is correct, expected, intended result for our product.

Documentation is a powerful tool to protect yourself from long and wasteful discussions that happen after someone sees a surprising behavior in your product.

So, it is not only very important to keep your documentation maintained but also beneficial.
Not only for your users, but also yourself.

## Where is a Bug?

To improve your documents continuously, let's introduce an idea of "documentation bug".

It is quite frequent to spend a very long time to discuss whether a certain surprising behavior of our product.

Without having a documentation, any software product with a meaningful size cannot be operated.
If so, even if the surprising behavior is justified after such a long discussion, isn't there anything wrong?

Yes, the fact that the product is missing a proper documentation is already a problem.
Then, why don't we think that it's a bug in your documentation, which is a part of your product?

This is the thought behind the bug handling process that this page defines.

## Enhancement or Bug?

When you make a change in the source code, let's think of these rules.:

1. If it is an enhancement, it should come with a new documentation.
   (Or very, very trivial improvement)
2. If it is a bug, the existing behavior should be violating some written specification.
   (Or very, very obvious)

In the case 1., you should write an addition to the documentation. 
In the case 2., you should include a pointer to the document that the existing behavior is violating. 

If it seems difficult to make your pull request follow the rules, consult with ukai.hiroshi@moneyforward.co.jp.

Let's think of every ticket/issue you work on is an occasion to improve your documentation.
