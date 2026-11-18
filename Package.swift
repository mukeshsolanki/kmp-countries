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
            url: "https://github.com/mukeshsolanki/kmp-countries/releases/download/0.2.0/KmpCountries.xcframework.zip",
            checksum: "6634ecff310e9840787d203acab6aa8a92bfdba62578a4f19aa055a564f647fc"
        ),
        .binaryTarget(
            name: "KmpCountriesGeo",
            url: "https://github.com/mukeshsolanki/kmp-countries/releases/download/0.2.0/KmpCountriesGeo.xcframework.zip",
            checksum: "88bee5f0d71398dc7413d938f5789000787bd25356561d8c580f3d89daae12e8"
        ),
    ]
)
