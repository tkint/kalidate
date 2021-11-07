# kalidate

Kalidate is a kotlin library allowing to verify the validity of an object.

It is particularly handy when dealing with forms when the app is used as a backend by a frontend
application.

## Getting started


## Simple example

Given the following data class:
```kotlin
data class ShopItem(
    val label: String, 
    val description: String?, 
    val price: Double,
)
```

You may want to verify that:
 - the label is at least 5 characters long and not exceeding 20.
 - the description is at least 15 characters long if provided
 - the price is not a negative value

You can then validate this rules by doing the following:
```kotlin
val shopItem = ShopItem(
    label = "This is a label",
    description = "This item is a wonderful piece of art, you should buy it !",
    price = 20.0,
)

val errors = shopItem.validate {
    listOf(
        ::label.min(5).max(20),
        ::description.min(15),
        ::price.min(0),
    )
}
```
