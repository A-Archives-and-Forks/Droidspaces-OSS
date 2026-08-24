# DESIGN-TODO.md

Places where the Android app does not follow [DESIGN.md](./DESIGN.md) yet.

The list this file opened with has been worked through. What is left is here, plus the things
that were looked at and deliberately left alone, because "we decided not to" is worth writing
down once so nobody re-opens it.

Paths are relative to `Android/app/src/main/java/com/droidspaces/app/`.

Component level duplication is tracked in the "Known duplicates" section of
[CONTRIBUTING.md](./CONTRIBUTING.md). This file is about visual values.

## Still open

- **`TerminalDialog` and `ProgressDialog` do not use `DsDialog`.** Deliberate for now: the first
  is three quarters of the screen with its own header row, the second is not dismissible. If a
  third dialog ever wants either shape, that is the moment to widen the shared one.
- **`ToggleCard` and `SwitchItem`** are two shapes of the same switch row. `SwitchItem` is used
  only by the settings screen.
- **`SummaryItem`** exists as three private overloads in `InstallationSummaryScreen.kt`.
- **`ContainersScreen`** hand-builds the typed confirmation gate that `ConfirmPhraseField`
  already provides, and inlines its own error tinted field colours because
  `DsTextFieldDefaults` has no error variant. The missing variant is the actual gap.
- **`labelFontSize` on `PrimaryActionBottomBar`.** One caller uses it, `RootCheckScreen`, to
  push its call to action to 16sp. Either the scale covers it or the parameter should go.

## Looked at, deliberately not changed

- **The init system status hues** (`statusColorFor()` in `ui/screen/InitServiceScreen.kt`) stay
  a hand picked traffic light rather than theme roles. Six of the seven states can sit in the
  legend row at once, and `primary` and `tertiary` move with dynamic colour and the palette
  picker, so running and abnormal would collapse onto nearly the same colour under some
  palettes. The four duplicate copies are gone, which was the real problem.
- **The terminal virtual keys background** (`ui/screen/ContainerTerminalScreen.kt`) stays a
  literal. It sits against the terminal's own black, which does not follow the app theme, so a
  surface role would give a light strip under a dark terminal.
- **The root check button's disabled colours** (`ui/screen/RootCheckScreen.kt`) stay equal to
  its enabled colours. It is disabled only while a check is in flight, and greying it out for
  that moment reads as a flicker.
- **The unit detail and override editor titles** keep a smaller style than every other screen
  title. Both display systemd unit names, which are long, so `titleLarge` would only ellipsize
  sooner.
- **`EmptyState` is not adopted** by `ui/screen/AutoBootPriorityScreen.kt` or the private empty
  state in `ui/screen/InitServiceScreen.kt`. Absorbing them needs roughly seven new parameters
  for two call sites, which makes the shared component worse than the duplication.
  `ui/component/ContainerUsersCard.kt` is not an empty state at all, it is a plain `Text` in a
  card body, and forcing a `fillMaxSize` component into it would break the card.
- **Faint fill alphas of 0.03, 0.04 and 0.06** in the terminal dialog, the terminal screen and
  the sparse image screen. They are one to three percent apart from each other and invisible in
  isolation. Snapping them to a shared value would mean inventing a token for something nobody
  can see.

## Not verified on a device

Everything from the cleanup pass compiles, and the values were read back out of the source,
but none of it has been seen running. Worth a look, roughly in order of how visible the change
is:

1. **Rotate to landscape and open every dialog.** This is the one that matters: the actions must
   be present, full height, equal width, with the body scrolling behind them. Do it both ways,
   opening the dialog in landscape and rotating while it is open, since the activity handles
   configChanges and those are different code paths. Do it with the keyboard up on the ones with
   fields (add port forward, environment variables, repo manager, sparse resize) and on the long
   ones (language picker, file picker, About, uninstall). A short dialog (uninstall) must stay
   short in portrait, not stretch to the cap.
2. Dialog widths, now that fifteen dialogs share one shell. The four that used to be `0.92f`
   and the one on `0.95f` are slightly narrower, and the notification permission dialog is
   wider. Worth opening a few in a row to check they agree.
3. Every dialog's Cancel and Confirm row, rebuilt on the v6.4.0 footer. Worth checking the two
   destructive dialogs (uninstall, hardware access) for the error fill and its label colour, a
   disabled confirm, and the notification permission dialog that prompted the change.
4. The six status dots in the systemd legend row, which now come from one function.
5. The three bottom action bars, and the save button's three states on the edit and auto boot
   screens.
6. The error logs dialog, which no longer has a red fill and whose single button is now the
   dismiss button from the footer.
7. The container card header, 16dp taller now that the logs button has a real touch target.
8. AMOLED mode, after the theme cache was flattened.
