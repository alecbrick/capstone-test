def main():
    g = open("extra.out", "r").read()
    f = open("err.out", "r").read()
    feedback = "Grader did not write to output file. Make sure you don't include the grader files or call System.exit().\\nStdout:\\n"

    g = g.replace("\n", "\\n").replace("\"", "\\\"").replace("\t", "\\t")
    f = f.replace("\n", "\\n").replace("\"", "\\\"").replace("\t", "\\t")
    feedback += g
    feedback += "\\nStderr:\\n"
    feedback += f
    feedback += "\\nIf this appears to be in error, please submit a bug report."

    print "{\"fractionalScore\": 0.0, \"feedback\": \"%s\"}" % feedback

if __name__ == "__main__":
    main()
