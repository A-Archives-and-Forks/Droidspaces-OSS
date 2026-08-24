# DESIGN-TODO.md

Places where the Android app does not follow [DESIGN.md](./DESIGN.md) yet.

This is a backlog, not a style debate. Every item names a file, a current value and a target
value, so any of them can be picked up on its own. Nothing here blocks a feature PR, but a PR
that touches one of these files should fix the item it touches.

Paths are relative to `Android/app/src/main/java/com/droidspaces/app/`.

Component level duplication (three copies of the same bottom bar, the missing shared dialog
shell) is tracked in the "Known duplicates" section of [CONTRIBUTING.md](./CONTRIBUTING.md).
This file is about visual values.

## Correctness first

These are not cosmetic and jump the queue.

- `ui/component/ContainerCard.kt:137` uses `IconButton(modifier = Modifier.size(32.dp))`. Below
  the 48dp minimum touch target. The icon inside can stay 20dp, the target cannot stay 32dp.

## Hardcoded colour

The largest single break from the theme. All four dynamic colour paths, the six palettes and
AMOLED are bypassed at these sites.

- `ui/screen/InitServiceScreen.kt:383-389` defines a traffic light palette as raw hex
  (`0xFF4CAF50`, `0xFFFFCA28`, `0xFFEF5350`, `0xFFFF7043`, `0xFF607D8B`, `0xFF90A4AE`). The same
  literals are re-typed in `ui/screen/SystemdScreen.kt:65-69`, `ui/screen/OpenRCScreen.kt:49-52`
  and `ui/screen/ProcdScreen.kt:51-55`. Four copies of one palette. Target: the semantic roles in
  DESIGN.md, running to `primary`, failed to `error`, transitioning to `tertiary`, and one shared
  mapping function instead of four.
- `ui/component/DroidspacesStatusCard.kt:62` uses `Color(0xFFFFB300)` for "update available".
  Target: `tertiary`.
- `ui/screen/ContainerTerminalScreen.kt:339` uses `Color(0xFF1A1A1E)`, which is a copy of
  `surfaceContainerLow`'s dark base from `Theme.kt`. Target: the role.
- `ui/theme/Color.kt:6-21` holds twelve legacy constants marked "backward compatibility". Only
  `AMOLED_BLACK` is referenced. Delete the other eleven.

The ANSI table in `util/AnsiColorParser.kt` is terminal emulation and stays as it is.

## Shape

- Dialog shells at 28dp: `ui/component/TerminalDialog.kt:45`,
  `ui/screen/ContainerTerminalScreen.kt:450`, and `ShapeUtils.DIALOG_SHAPE` as used by
  `ProgressDialog` and `ErrorLogsDialog` in `ui/util/DialogUtils.kt:58,108`. Target: 24.
- `AboutDialog` at `ui/screen/SettingsScreen.kt:578` uses 20dp, the card radius. Target: 24.
- The 14dp button family: `ui/screen/ContainerDetailsScreen.kt:450,656,676,689`,
  `ui/component/ContainerUsersCard.kt:292`, `ui/component/RootfsRepoSheet.kt:714`,
  `ui/component/EnvironmentVariablesDialog.kt:188`, `ui/screen/SettingsScreen.kt:953`. Seventeen
  sites in total. Target: 16.
- Text fields inside dialogs at 14dp: `ui/component/EnvironmentVariablesDialog.kt:66`,
  `ui/component/DangerousActionConfirm.kt:79`, `ui/component/FilePickerDialog.kt:222`,
  `ui/component/RootfsRepoSheet.kt:652,800`, `ui/screen/ContainersScreen.kt:555`. Target: 16, the
  value every field outside a dialog already uses.
- `ui/component/DsDropdown.kt:38` gives the dropdown field a 20dp radius while the
  `OutlinedTextField` beside it in the same form is 16dp. Visible mismatch. Target: 16 for the
  field, the 20dp menu surface stays.
- Material buttons with no `shape` argument render as full pills, which exists nowhere else in
  the app: `ui/screen/InstallationProgressScreen.kt:149,169,177` and
  `ui/screen/RequirementsScreen.kt:462`. Target: 16, or replace with a clickable `Surface`.
- `ui/component/ContainerConfigForm.kt:683` uses `RoundedCornerShape(6.dp)`, the only 6dp in the
  tree. Target: 8.
- Two selectable radio rows in dialogs disagree: `ui/screen/SettingsScreen.kt:925` is 12dp,
  `ui/screen/ContainerTerminalScreen.kt:505` is 16dp. Pick one, probably 16.

## Border alpha

Six values are in use where DESIGN.md defines one per element type: `0.2f` (12 sites), `0.25f`
(8), `0.3f` (23), `0.35f` (30), `0.4f` (17), `0.5f` (20). Most of the spread is legitimate under
the table in DESIGN.md, the work is finding the sites that are not.

- `ui/screen/ContainerDetailsScreen.kt` uses `0.5f` on card borders throughout while
  `ui/screen/RequirementsScreen.kt` uses `0.35f` for the same kind of card. Target: `0.35f`.
- `ui/component/DsDropdown.kt:39` inlines an `OutlinedTextFieldDefaults.colors(...)` that is
  `DsTextFieldDefaults.colors()` with `unfocusedBorderColor` at `0.35f` instead of `0.5f`. Target:
  use the shared defaults.
- `ui/component/SettingsCard.kt:41` and `ui/component/ToggleCard.kt:26` use `0.5f` as the disabled
  multiplier where the rest of the app uses `0.38f`. `ToggleCard.kt:38` then computes `0.7f * alpha`.
  Target: the M3 pair, `0.38f` content and `0.12f` container.
- `ui/component/DialogFooterRow.kt:62,71` uses three bespoke fills (`0.06f`, `0.15f`, `0.08f`).
  Worth a look, though the confirm button tint is arguably deliberate.
- `cancelBorderAlpha` is a public parameter of `DialogFooterRow` (`:37`). An alpha exposed as
  component API. Target: drop the parameter, use the table value.
- Singletons with no rationale: `ui/component/TerminalDialog.kt:184` at `0.03f`,
  `ui/screen/ContainerTerminalScreen.kt:507` at `0.04f`,
  `ui/screen/SparseImageConfigScreen.kt:244` at `0.06f`, and five one-off values inside
  `ui/component/RootfsRepoSheet.kt`.
- `ui/util/DialogUtils.kt:173` uses `onErrorContainer` as a container fill. The only inverted role
  in the app.
- `ui/screen/RootCheckScreen.kt:89-90` sets `disabledContainerColor = primary` and
  `disabledContentColor = onPrimary`, so the disabled state looks identical to the enabled one.
  Either a bug or an undocumented decision.

## Typography

- `TopAppBar` titles use four different weights: Black at `ui/screen/SettingsScreen.kt:129`, Bold
  at `ui/screen/InitServiceScreen.kt:214`, SemiBold at `ui/screen/RequirementsScreen.kt:84` and
  `ui/screen/ContainerDetailsScreen.kt:159`, and no style at all on six screens
  (`EditContainerScreen.kt:162`, `ContainerConfigScreen.kt:80`, `ContainerNameScreen.kt:95`,
  `SparseImageConfigScreen.kt:89`, `AutoBootPriorityScreen.kt:107`,
  `InstallationSummaryScreen.kt:44`). Target: `titleLarge` Bold everywhere.
- `CenterAlignedTopAppBar` on three screens against plain `TopAppBar` on ten. Pick one.
- Four section header implementations, none shared: `ui/screen/SparseImageConfigScreen.kt:477`
  (`SectionLabel`, whose own KDoc says it exists to match the config form),
  `ui/component/ContainerConfigForm.kt:230,268,430,492,569` (inline, `titleMedium`),
  `ui/screen/SettingsScreen.kt:155,293,432,497` (inline, `titleSmall` Bold, and `:609,731` switch
  to SemiBold with no padding), `ui/screen/UnitDetailScreen.kt:142` (`SectionCard`). Target: one
  shared `SectionHeader` in `ui/component/`, `titleSmall` Bold `primary`.
- `JetBrainsMono` is declared locally three times instead of imported from `ui/theme/Type.kt:120`:
  `ui/screen/InitServiceScreen.kt:113` (which shadows the name), `ui/screen/UnitDetailScreen.kt:29`,
  `ui/screen/OverrideEditorScreen.kt:26`. Also listed in CONTRIBUTING.md.
- Two `fontSize` overrides that restate the scale value and do nothing:
  `ui/screen/ContainerDetailsScreen.kt:348` (11sp on `labelSmall`) and `:359` (12sp on `bodySmall`).
- `ui/component/PercentCircle.kt:32` uses `12.7.sp`. Also the whole file is unreferenced, see
  Dead code below.
- `ui/screen/MainTabScreen.kt:661` uses 10sp, below the 11sp floor of the scale.
- `ui/screen/InitServiceScreen.kt:427` picks a font size from the string length
  (`if (name.length > 25) 13.sp else 16.sp`). Target: fixed size with `TextOverflow.Ellipsis`.
- `ui/component/ContainerCard.kt:239,264` uses Bold on button labels where every dialog button is
  SemiBold. `ui/component/HelpCard.kt:44` uses Bold on a card title where `SettingsCard` uses
  SemiBold.

## Spacing

- The gap between cards in a list is 16 on most screens, 12 in
  `ui/screen/ContainerDetailsScreen.kt:179`, 10 in `ui/component/RootfsRepoSheet.kt:276` and 8 in
  `ui/screen/AutoBootPriorityScreen.kt:259`. Target: 16.
- `ui/component/HelpCard.kt:38` uses 20dp card inner padding against the 16dp standard.
- `ui/component/ContainerCard.kt:101` and seven siblings use
  `padding(horizontal = 16.dp, vertical = 14.dp)`. 14 is off scale. Target: 12 or 16.
- `ui/component/ContainerConfigForm.kt:234` uses `top = 8.dp` where its three sibling section
  headers use `top = 16.dp`.
- Off scale dp values in general: 14 (35 uses), 10 (25), 6 (23), 13 (7), 22 (6). Most are icon
  sizes and paddings that should snap to the nearest scale value.
- Icon sizes off the scale: 13dp at seven sites, four of them in `ui/component/RootfsRepoSheet.kt`,
  and 22dp at five sites. Target: 12, 16 or 20.

## Hand-rolled copies of shared components

These duplicate a component that already exists. Listed here because each one is also a visual
deviation, not only a maintenance problem.

- `PrimaryActionBottomBar` is reproduced inline in `ui/screen/ContainerConfigScreen.kt:89-137`,
  `ui/screen/EditContainerScreen.kt:171-235` and `ui/screen/AutoBootPriorityScreen.kt:120`. About
  50 lines each. The shared component is missing only the animated saved and saving states, so add
  those to it rather than keeping three copies.
- `StatusPill` is copied verbatim at `ui/screen/InitServiceScreen.kt:433-448`, same alphas, same
  dot, same padding.
- `EmptyState` is re-implemented at `ui/screen/AutoBootPriorityScreen.kt:214-238` (48dp icon and
  `outline` tint instead of 64dp and `onSurfaceVariant @ 0.6f`, and no title),
  `ui/screen/InitServiceScreen.kt:588-596`, and reduced to a bare `Text` at
  `ui/component/ContainerUsersCard.kt:214-218`.
- The `DsDropdown` dark menu theming block is duplicated at
  `ui/screen/InitServiceScreen.kt:536-546` against `ui/component/DsDropdown.kt:74-96`, including
  the same `luminance() < 0.5f` check, which re-derives "is dark" instead of reading theme state.
- Two dialog footers built inline instead of `DialogFooterRow`:
  `ui/screen/SettingsScreen.kt:951-990` (no wrapper surface, solid `primary` confirm) and
  `ui/screen/ContainerTerminalScreen.kt:540-570` (`TextButton` plus Material `Button`).
- `ui/util/DialogUtils.kt:107` is the only `Card()` and the only `CardDefaults` in the app, with a
  `FilledTonalButton` footer that is a fourth footer style.
- `MainActivity.kt:78` is the only `AlertDialog` in the app.

## Dead code

- `ui/component/PercentCircle.kt` has zero call sites.
- `ui/util/DialogUtils.kt:26` is an empty `object DialogUtils` whose entire body is the comment
  `// Use ShapeUtils for consistency`.
- `surfaceContainerLowest`, `inverseSurface`, `inverseOnSurface` and `inversePrimary` are defined
  in both schemes and never used. Harmless, but worth knowing when reading `Theme.kt`.
- `AmoledColorCache` in `ui/theme/Theme.kt:135-224` caches a single palette by name while
  `DroidspacesTheme` already wraps the whole thing in `remember(...)` at `:239`. The cache is
  redundant with the memo above it and carries a near duplicate of `blend` called `fastBlend`.

## Decide: what to do about ShapeUtils

`ui/util/DialogUtils.kt:33-40` defines six shape constants. Nothing outside that file references
them, and three of the six contradict what the app actually does:

| Constant | Defined | The app uses |
| --- | --- | --- |
| `DIALOG_SHAPE` | 28 | 24 |
| `CARD_SHAPE` | 16 | 20 |
| `BUTTON_SHAPE` | 12 | 16 |

Worse, 12dp is specifically the action pill *wrapper* radius, so a contributor following
`BUTTON_SHAPE` would produce exactly the mismatch DESIGN.md warns against.

Two options, and this needs a decision before either is worth doing:

1. Correct the values to match DESIGN.md, move the object somewhere sensible like
   `ui/theme/Shape.kt`, and adopt it across all 155 `RoundedCornerShape` literals.
2. Delete it, and let DESIGN.md be the only place the numbers live.

Until then, `CONTRIBUTING.md` no longer tells contributors to use it.
