// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "KmpCountries",
    platforms: [.iOS(.v15)],
    products: [
        .library(name: "KmpCountries", targets: ["KmpCountries"]),
        .library(name: "KmpCountriesGeo", targets: ["KmpCountriesGeo"]),
    ],
    targets: [
        .binaryTarget(
            name: "KmpCountries",
            url: "https://github.com/mukeshsolanki/kmp-countries/releases/download/0.1.0/KmpCountries.xcframework.zip",
            checksum: "ffe5abc42f4d03339f26c5696a11dfd7647bd3b3c687e3fb67068b595ad4ff04"
        ),
        .binaryTarget(
            name: "KmpCountriesGeo",
            url: "https://github.com/mukeshsolanki/kmp-countries/releases/download/0.1.0/KmpCountriesGeo.xcframework.zip",
            checksum: "456b00a8452e0e909b4753da3c5a1bdf7e00c1fdae13b053b75a71fdea7ddf71"
        ),
    ]
)
