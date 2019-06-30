# Weight Tracker

Weight Tracker is a Demo app for exploring the different options in Android testing.

Slides are [Here](https://www.slideshare.net/roisagiv/android-automated-testing)

## Installation

1. Go to [airtable.com](https://airtable.com/) and create a new workspace with 2 tables (Weights, WeightsTests).

Each table contains 3 columns: `Date`, `Weight` and `Notes`

2. Add to the `local.properties` files containing the following:

```properties
airtable.url=https://api.airtable.com/v0/YOUR_APP/Weights/
airtable.key=YOUR_KEY
airtable.e2e.url=https://api.airtable.com/v0/YOUR_APP/WeightsTests/
airtable.e2e.key=YOUR_KEY
E2E=false
```

## End to End tests

To run the end to end tests set `E2E=true` in `local.properties` files and recompile the app.

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License

[MIT](https://choosealicense.com/licenses/mit/)
