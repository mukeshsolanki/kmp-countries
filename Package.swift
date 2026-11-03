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
            url: "https://github.com/mukeshsolanki/kmp-countries/releases/download/0.0.0/KmpCountries.xcframework.zip",
            checksum: "0000000000000000000000000000000000000000000000000000000000000000"
        ),
        .binaryTarget(
            name: "KmpCountriesGeo",
            url: "https://github.com/mukeshsolanki/kmp-countries/releases/download/0.0.0/KmpCountriesGeo.xcframework.zip",
            checksum: "0000000000000000000000000000000000000000000000000000000000000000"
        ),
    ]
)
