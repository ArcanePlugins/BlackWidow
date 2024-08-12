from typing import Optional, List, Dict, Set
from enum import Enum
from collections.abc import Iterable

### DEBUGGING ###
debug_logs: bool = False

def _debug_log(
    txt: str
) -> None:
    if not debug_logs:
        return
    print("[DEBUG]", txt)

### TEST PARAMETERS ###


class Color(Enum):
    RED:   str = "\033[0;31m"
    GREEN: str = "\033[0;32m"
    RESET: str = "\033[0m"

class Policy(Enum):
    ALLOW: bool = False
    DENY:  bool = True

    @staticmethod
    def default() -> "Policy":
        return Policy.ALLOW
    
class RuleMatchResult():
    def __init__(
        self,
        match: bool,
        desc: str,
    ) -> None:
        self.match = match
        self.desc = desc

    def __repr__(self) -> str:
        return f"{self.match} determined via: {self.desc}"

class Evaluation():
    def __init__(
        self,
        policy: Policy,
        desc: str,
        chain: Optional["Chain"],
    ) -> None:
        self.policy = policy
        self.desc = desc

    def __repr__(self) -> str:
        return f"{self.policy.name} evaluation via: {self.desc}"
    
class Chain():
    def __init__(
        self,
        policy: Policy,
        rules: Iterable[str],
    ) -> None:
        self.policy = policy
        self.rules = rules

    def __repr__(self) -> str:
        return f"{self.policy.name} chain with {len(self.rules)} rules"

    @staticmethod
    def _match_rule(
        cmd: str,
        rule: str
    ) -> RuleMatchResult:
        cmd_args:      list[str] = cmd[1:].lower().strip().split()
        cmd_args_len:  int       = len(cmd_args)
        rule_args:     list[str] = rule[1:].lower().strip().split() 
        rule_args_len: int       = len(rule_args)  
        min_len:       int       = min({cmd_args_len, rule_args_len,})

        _debug_log(f"Is Match: Checking cmd={cmd}, rule={rule}, min_len={min_len}")
        _debug_log(f"Is Match: Cmd args (len={cmd_args_len}): {cmd_args}")
        _debug_log(f"Is Match: Rule args (len={rule_args_len}): {rule_args}")

        if rule_args_len == 0 and cmd_args_len == 0:
            _debug_log("Is Match: True, because rule and cmd args length are both 0.")
            return RuleMatchResult(True, "the rule and cmd are both empty")

        if rule_args_len > cmd_args_len:
            _debug_log("Is Match: False, impossible to match as 'rules args len' > 'cmd args len'.")
            return RuleMatchResult(False, "rule has more args than cmd, thus impossible to match")

        for i in range(min_len):
            cmd_arg:  str = cmd_args[i]
            rule_arg: str = rule_args[i]

            _debug_log(f"Is Match [i={i}]: cmd_arg={cmd_arg}, rule_arg={rule_arg}")

            # if the args don't match and the rule arg isn't a wildcard, nope
            if cmd_arg != rule_arg and rule_arg != "*":
                _debug_log(f"Is Match: False (Not arg match and not wildcard)")
                return RuleMatchResult(False, f"arg {i} not matched, and rule arg is not wildcard")
            
            # if we're looking at the last arg of the rule, we've found a match.
            if i + 1 == rule_args_len:
                _debug_log(f"Is Match: True (Matched all args of rule)")
                return RuleMatchResult(True, "cmd matches rule")
            
            # otherwise, check that all next args (if present) match
            
        # fallback to nope
        _debug_log(f"Is Match: False (no arg matches)")
        return RuleMatchResult(False, "rule does not match")

    def matches(
        self,
        cmd: str,
    ) -> RuleMatchResult:
        _debug_log(f"Has Match: Checking cmd='{cmd}' on chain, len='{len(self.rules)}'")

        for rule in self.rules:
            _debug_log(f"Has Match: Checking cmd='{cmd}' on rule='{rule}'")
            res: RuleMatchResult = Chain._match_rule(cmd, rule)
            if res.match:
                _debug_log(f"Has Match: Matched!")
                return RuleMatchResult(True, f"in rule '{rule}'; {res.desc}")
            _debug_log(f"Has Match: No match, continuing")
        return RuleMatchResult(False, "no rule matched")

TEST_PLUGIN_CMDS: Set[str] = {
    "/plugins", "/pl", "/version", "/ver", "/icanhasbukkit",
    "/about", "/?", "/help", "/ehelp", "/paper", "/spigot"
}

TEST_CHAINS: List[Chain] = [
    Chain(Policy.DENY, ["/cd reload *", "/"]),
    Chain(Policy.ALLOW, ["/cd reload"]),
    Chain(Policy.DENY, ["/cd"]),
    Chain(Policy.DENY, list(TEST_PLUGIN_CMDS)),
    Chain(Policy.ALLOW, ["/es version"]),
    Chain(Policy.DENY, ["/es give", "/es enchant"]),
]

TEST_RULES: Dict[Policy, Set[str]] = {
    Policy.DENY: {
        "/cd abc", "/cd reloada", "/cd reloa", "/cd reload abc",
        "/plugins", "/pl", "/version", "/ver", "/icanhasbukkit",
        "/about", "/?", "/help", "/ehelp", "/paper", "/spigot",
        "/es give", "/es enchant", "/cd", "/"
        },
    Policy.ALLOW: {
        "/cd reload", "/CD RELOAD", "//",
        "/es", "/es version", "/abcdefg"
        },
}

### LOGIC ###

def evaluate(
    cmd: str,
    chains: List[Chain],
    default: Policy
) -> Evaluation:
    try:
        for i, chain in enumerate(chains):
            res: RuleMatchResult = chain.matches(cmd)
            
            if res.match:
                return Evaluation(chain.policy, f"in chain '{i}'; {res.desc}", chain)
    except BaseException as ex:
        print("An exception occurred whilst evaluating cmd:", cmd)
        print("DENYING command for best security practice in this case.")
        print(ex)
        return Evaluation(Policy.DENY, "Denying for security because of exception during evaluation", None)
    
    return Evaluation(default, "default policy because no chains matched", None)


### TESTING ###

def _test() -> None:
    print("Testing...")
    total: int = 0
    fails: int = 0
    for expected_policy, cmds in TEST_RULES.items():
        for cmd in cmds:
            res: Evaluation = evaluate(cmd, TEST_CHAINS, Policy.default())

            if res.policy != expected_policy:
                print(f"FAILURE on on {cmd}! Eval desc: {res.desc}")
                fails += 1
            total += 1
    fails_prop: float = round(fails * 100 / total, 2)
    s_or_f: str = (f"{Color.GREEN.value}TEST SUCCESSFUL" if fails == 0 else f"{Color.RED.value}TEST FAILED") + Color.RESET.value
    print(f"Testing complete. {fails} of {total} tests failed ({fails_prop}%). ({s_or_f})")

def _interactive_mode() -> None:
    global debug_logs

    print("Entering interactive mode. To exit, use 'q', 'x', 'quit', or 'exit'. To toggle debug, use 'd' or 'debug'.")
    try:
        while True:
            print("READY.")
            inp: str = input("> /")

            if inp.lower() in {"q", "x", "quit", "exit"}:
                print("Exiting interactive mode.")
                return
            elif inp.lower() in {"d", "debug"}:
                print(f"Toggling debug mode from {debug_logs} to {not debug_logs}.")
                debug_logs = not debug_logs
                continue

            res: Evaluation = evaluate("/" + inp, TEST_CHAINS, Policy.default())
            a_or_d: str = None
            match res.policy:
                case Policy.ALLOW:
                    a_or_d = Color.GREEN.value + 'Allowed'
                case Policy.DENY:
                    a_or_d = Color.RED.value + 'Denied'
                case _:
                    raise ValueError("Unexpected value:", res.policy)
            print(f"Result: {a_or_d}{Color.RESET.value} ({res.desc})")
            print()
    except KeyboardInterrupt:
        print("\nExiting interactive mode.")
        return

if __name__ == "__main__":
    _test()
    _interactive_mode()

