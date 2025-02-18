# Development Notes

## Patching System
Modern Paper forks (including Purpur) use a file-based patching system:

### API Patches
- Located in `purpur-api/paper-patches/files/`
- Contains patches for API source files

### Server Patches
1. Minecraft Patches (`purpur-server/minecraft-patches/`)
   - `features/` - Feature-specific patches
   - `sources/` - Direct source code modifications

2. Paper Patches (`purpur-server/paper-patches/`)
   - `features/` - Feature-specific patches
   - `files/` - Source code modifications

### Historical Patches
The `patches/` directory contains only historical patches from older versions:
- 1-20-6 (dropped)
- 1-21-1 (dropped)
- 1-21-3 (dropped)
- Some unapplied patches

## Reference
- Purpur reference implementation can be found at `~/purpur-reference`
- Clean Purpur clone at `~/purpur-clean`
- Version: 1.21.4 