# CSPICE CI/CD Documentation

This repository now includes automated CI/CD pipelines to build and release pre-compiled CSPICE libraries for multiple platforms.

## Automated Builds

### Release Workflow

The main release workflow (`.github/workflows/build-release.yml`) automatically builds CSPICE libraries for:

- **Linux x64**: Static library (`libcspice.a`)
- **macOS x64**: Static library (`libcspice.a`) 
- **Windows x64**: Static library (`cspice.lib`) and Dynamic library (`cspice.dll`)

### Triggering Releases

#### Automatic (via Git Tags)
```bash
git tag v1.0.0
git push origin v1.0.0
```

#### Manual (via GitHub UI)
1. Go to the "Actions" tab in GitHub
2. Select "Build and Release CSPICE Libraries" workflow
3. Click "Run workflow"
4. Enter a tag name (e.g., `v1.0.0`)
5. Click "Run workflow"

### Build Artifacts

Each release creates the following archives:

- `cspice-linux-x64.tar.gz` / `cspice-linux-x64.zip`
- `cspice-macos-x64.tar.gz` / `cspice-macos-x64.zip`
- `cspice-windows-static.tar.gz` / `cspice-windows-static.zip`
- `cspice-windows-dll.tar.gz` / `cspice-windows-dll.zip`

Each archive contains:
- Compiled library files
- Header files (`include/` directory)
- Platform-specific README files

### Testing

A test workflow (`.github/workflows/test-build.yml`) is available for testing the Linux build process without creating a release.

## Usage

### Linux/macOS
```bash
# Extract the archive
tar -xzf cspice-linux-x64.tar.gz
cd cspice-linux-x64

# Compile your program
gcc -I./include your_program.c -L. -lcspice -lm -o your_program
```

### Windows (Static Library)
```cmd
rem Extract cspice-windows-static.zip
rem In Visual Studio project settings:
rem - Add include/ directory to Additional Include Directories
rem - Add cspice.lib to Additional Dependencies
```

### Windows (Dynamic Library)
```cmd
rem Extract cspice-windows-dll.zip
rem In Visual Studio project settings:
rem - Add include/ directory to Additional Include Directories  
rem - Add cspice_dll.lib to Additional Dependencies
rem - Ensure cspice.dll is in your PATH or application directory
```

## Build Environment Details

### Linux Build
- Uses Ubuntu latest
- Requires `csh` shell
- Executes `mk_linux.csh` build script
- Produces `libcspice.a` (~10MB)

### macOS Build  
- Uses macOS latest
- Executes `mk_mac.csh` build script
- Produces `libcspice.a`

### Windows Build
- Uses Windows latest with MSVC
- Static build: executes `mk_static.bat` → produces `cspice.lib`
- DLL build: executes `mk_dll.bat` → produces `cspice.dll`, `cspice_dll.lib`, `cspice_dll.exp`

## Troubleshooting

### Build Failures
1. Check the Actions tab for detailed build logs
2. Common issues:
   - Missing dependencies (automatically handled in CI)
   - Script permissions (automatically handled)
   - Compiler errors (indicates source code issues)

### Release Issues
1. Ensure tag names follow semantic versioning (e.g., `v1.0.0`)
2. Check that all build jobs completed successfully before release creation
3. Verify GitHub token permissions for release creation

## Development

### Testing Changes
1. Use the test workflow to verify Linux builds work
2. Create a draft release to test the full workflow
3. Always test locally first when possible

### Modifying Build Process
- Build scripts are in `src/` directory
- Workflow configurations are in `.github/workflows/`
- Do not modify existing build scripts to maintain compatibility
- Add new build variants by extending the matrix strategy